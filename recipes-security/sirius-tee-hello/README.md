# sirius-tee-hello

A deliberately small OP-TEE learning example for `meta-sirius`.

It demonstrates the most important OP-TEE application model:

- a **Normal World** Linux program: `/usr/bin/sirius-tee-hello`
- a **Secure World** Trusted Application (TA):
  `/usr/lib/optee_armtz/7f8c9d52-61aa-4a9f-9c4e-8c7d3fb84221.ta`

The example sends an integer from Linux to the TA. The TA increments the value
inside Secure World and sends it back.

## What this example teaches

This is not intended to be a production security feature. It is a learning
example that shows:

1. how a normal Linux application connects to OP-TEE;
2. how a UUID identifies a Trusted Application;
3. how `TEEC_OpenSession()` selects the TA;
4. how `TEEC_InvokeCommand()` sends a command and parameters to Secure World;
5. how `TA_InvokeCommandEntryPoint()` receives the command in the TA;
6. how a result comes back from Secure World to Linux.

## Runtime architecture

```text
                       NORMAL WORLD / LINUX

shell
  |
  | run
  v
/usr/bin/sirius-tee-hello
  |
  | TEEC_InitializeContext()
  | TEEC_OpenSession(UUID)
  | TEEC_InvokeCommand(INCREMENT)
  v
libteec
  |
  v
/dev/tee0
  |
===================== TRUSTZONE =====================
  |
  v
OP-TEE OS
  |
  | load TA with matching UUID
  v
7f8c9d52-61aa-4a9f-9c4e-8c7d3fb84221.ta
  |
  v
TA_InvokeCommandEntryPoint()
  |
  | 41 -> 42
  v
return result to Linux
```

## Source layout

```text
sirius-tee-hello/
├── README.md
└── recipes-security/
    └── sirius-tee-hello/
        ├── sirius-tee-hello_1.0.bb
        └── files/
            ├── host/
            │   └── main.c
            ├── include/
            │   └── sirius_tee_hello_ta.h
            └── ta/
                ├── Makefile
                ├── sub.mk
                ├── hello_ta.c
                └── user_ta_header_defines.h
```

## UUID

The example uses this fixed UUID:

```text
7f8c9d52-61aa-4a9f-9c4e-8c7d3fb84221
```

The UUID appears in the shared header and becomes the TA filename:

```text
/usr/lib/optee_armtz/
7f8c9d52-61aa-4a9f-9c4e-8c7d3fb84221.ta
```

The Linux client does not execute this file with `exec()` like a normal binary.
It passes the same UUID to `TEEC_OpenSession()`. OP-TEE then identifies and
loads the matching TA.

## Host application

`files/host/main.c` is an ordinary Linux program.

Its important calls are:

```c
TEEC_InitializeContext(...);
TEEC_OpenSession(...);
TEEC_InvokeCommand(...);
TEEC_CloseSession(...);
TEEC_FinalizeContext(...);
```

The program sends one `uint32_t` using a `TEEC_VALUE_INOUT` parameter.

Default input:

```text
41
```

Expected output:

```text
42
```

You can also supply your own value:

```sh
sirius-tee-hello 100
```

The TA should return `101`.

## Trusted Application

`files/ta/hello_ta.c` is code that runs under OP-TEE in Secure World.

A user TA exposes standard OP-TEE entry points:

```text
TA_CreateEntryPoint()
TA_DestroyEntryPoint()
TA_OpenSessionEntryPoint()
TA_CloseSessionEntryPoint()
TA_InvokeCommandEntryPoint()
```

For this example, `TA_InvokeCommandEntryPoint()` accepts one command:

```text
SIRIUS_TEE_HELLO_CMD_INCREMENT = 0
```

It receives the integer from the Normal World application and performs:

```c
params[0].value.a++;
```

The modified value is returned through the same INOUT parameter.

## Yocto/BSP assumptions

This recipe is designed around the same OP-TEE layout used by the NXP i.MX
Kirkstone OP-TEE 3.19.0 recipes.

The TA development kit is expected at:

```text
${STAGING_INCDIR}/optee/export-user_ta_${OPTEE_ARCH}
```

where an AArch64 build uses:

```text
export-user_ta_arm64
```

The machine must expose the `optee` machine feature because the recipe uses:

```bitbake
REQUIRED_MACHINE_FEATURES = "optee"
```

## Add to meta-sirius

Copy the recipe directory into the root of `meta-sirius` so the final layout is:

```text
meta-sirius/
└── recipes-security/
    └── sirius-tee-hello/
        ├── sirius-tee-hello_1.0.bb
        └── files/
            ...
```

The top-level README in this archive is only documentation; you can keep it,
move its content to your project docs, or remove it before committing.

## Check prerequisites

Before building:

```sh
bitbake -e sirius-tee-hello | grep '^REQUIRED_MACHINE_FEATURES='
bitbake -e sirius-tee-hello | grep '^TA_DEV_KIT_DIR='
```

On an i.MX8MP AArch64 build, the second command should resolve to an OP-TEE TA
dev-kit directory ending in:

```text
export-user_ta_arm64
```

You can also verify OP-TEE recipes are available:

```sh
bitbake-layers show-recipes | grep -A3 -B1 optee
```

## Build only the example

```sh
bitbake sirius-tee-hello
```

After the build, inspect the package contents:

```sh
oe-pkgdata-util list-pkg-files sirius-tee-hello
```

Expected important files:

```text
/usr/bin/sirius-tee-hello
/usr/lib/optee_armtz/7f8c9d52-61aa-4a9f-9c4e-8c7d3fb84221.ta
```

## Add it to an image

For temporary testing in `local.conf`:

```bitbake
IMAGE_INSTALL:append = " sirius-tee-hello"
```

For a real `meta-sirius` image, add `sirius-tee-hello` through the image recipe
or packagegroup used by that image instead of keeping it in `local.conf`.

Then rebuild the image and flash/update the device.

## Device prerequisites

OP-TEE itself must already be working. Useful checks are:

```sh
ls -l /dev/tee*
systemctl status tee-supplicant
```

Typical devices include:

```text
/dev/tee0
/dev/teepriv0
```

The supplicant should be active.

## Verify installed files

```sh
which sirius-tee-hello
ls -l /usr/lib/optee_armtz/7f8c9d52-61aa-4a9f-9c4e-8c7d3fb84221.ta
ldd /usr/bin/sirius-tee-hello
```

The host binary should link against `libteec`.

## Run

```sh
sirius-tee-hello
```

Expected Normal World output:

```text
Sirius OP-TEE hello example
Normal world : starting Linux client application
Normal world : OP-TEE context initialized
Normal world : session opened with Sirius Trusted Application
Normal world : sending value 41 to secure world
Normal world : secure world returned 42
SUCCESS      : OP-TEE round trip verified
```

Or:

```sh
sirius-tee-hello 100
```

Expected result includes:

```text
Normal world : sending value 100 to secure world
Normal world : secure world returned 101
```

## What happens when the command runs?

### Step 1 - Linux starts the host binary

The shell executes:

```text
/usr/bin/sirius-tee-hello
```

This remains a normal Linux userspace program.

### Step 2 - Connect to OP-TEE

The client executes:

```c
TEEC_InitializeContext(NULL, &ctx);
```

`libteec` talks to the Linux TEE driver.

### Step 3 - Open the Sirius TA

The client executes `TEEC_OpenSession()` using the Sirius UUID.

Conceptually:

```text
Please open TA:
7f8c9d52-61aa-4a9f-9c4e-8c7d3fb84221
```

### Step 4 - TA loading

OP-TEE requests the matching TA. `tee-supplicant` can provide the TA file from
the Linux filesystem:

```text
/usr/lib/optee_armtz/
7f8c9d52-61aa-4a9f-9c4e-8c7d3fb84221.ta
```

The file is stored in the Linux filesystem, but the TA code executes under
OP-TEE in Secure World.

### Step 5 - Invoke command

The host sends:

```text
command = SIRIUS_TEE_HELLO_CMD_INCREMENT
value   = 41
```

through `TEEC_InvokeCommand()`.

### Step 6 - Secure-world dispatch

OP-TEE calls:

```c
TA_InvokeCommandEntryPoint(...)
```

inside the TA.

The TA checks the command and executes:

```c
params[0].value.a++;
```

### Step 7 - Return to Linux

The result returns through the OP-TEE stack to `TEEC_InvokeCommand()`.

The host sees:

```text
42
```

and verifies that it equals `41 + 1`.

## Important conceptual point

Running:

```sh
sirius-tee-hello
```

does **not** move the Linux process into Secure World.

Instead:

```text
Linux application        Trusted Application
-----------------        -------------------
sirius-tee-hello   --->  UUID.ta
normal world             secure world
```

Only the code in the `.ta` executes as the Trusted Application.

This is the pattern that later scales to useful security services such as:

```text
Linux cloud agent
       |
       | SIGN(data)
       v
OP-TEE Trusted Application
       |
       | uses protected private key
       v
returns signature only
```

## Debugging

### TEEC_InitializeContext fails

Check:

```sh
ls -l /dev/tee*
```

If `/dev/tee0` is missing, solve the OP-TEE boot/kernel integration before
troubleshooting this example.

### TEEC_OpenSession returns ITEM_NOT_FOUND

Check that the exact TA file exists:

```sh
ls -l /usr/lib/optee_armtz/7f8c9d52-61aa-4a9f-9c4e-8c7d3fb84221.ta
```

Also check:

```sh
systemctl status tee-supplicant
journalctl -u tee-supplicant
```

### Recipe cannot find the TA dev kit

Check:

```sh
bitbake -e sirius-tee-hello | grep '^TA_DEV_KIT_DIR='
```

For the NXP OP-TEE 3.19.0 i.MX Kirkstone model used while developing this
example, the recipe expects the OP-TEE development kit exported by `optee-os`
under the recipe sysroot.

### usrmerge

This recipe deliberately installs to:

```text
/usr/bin
/usr/lib/optee_armtz
```

through `${bindir}` and `${libdir}`. This avoids the `/bin` and `/lib`
`usrmerge` QA issue that can occur with older OP-TEE recipes.

## Next learning step

After this example works, a useful second example is secure key handling:

1. generate or import a key in a TA;
2. store it using OP-TEE secure storage;
3. expose a `SIGN` command;
4. allow Linux to pass data into the TA;
5. return only the signature, never the private key.

That architecture is much closer to a real embedded-product use case.

## References

- OP-TEE documentation: Trusted Applications
  https://optee.readthedocs.io/en/latest/building/trusted_applications.html
- OP-TEE examples
  https://github.com/linaro-swg/optee_examples
- NXP i.MX OP-TEE test repository
  https://github.com/nxp-imx/imx-optee-test

