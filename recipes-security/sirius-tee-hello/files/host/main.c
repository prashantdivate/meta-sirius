// SPDX-License-Identifier: BSD-2-Clause
#include <err.h>
#include <inttypes.h>
#include <stdio.h>
#include <stdlib.h>
#include <tee_client_api.h>

#include "sirius_tee_hello_ta.h"

int main(int argc, char *argv[])
{
    TEEC_Context ctx;
    TEEC_Session sess;
    TEEC_Operation op = { 0 };
    TEEC_Result res;
    TEEC_UUID uuid = SIRIUS_TEE_HELLO_UUID;
    uint32_t err_origin = 0;
    uint32_t value = 41;

    if (argc > 2) {
        fprintf(stderr, "Usage: %s [value]\n", argv[0]);
        return EXIT_FAILURE;
    }

    if (argc == 2) {
        char *end = NULL;
        unsigned long parsed = strtoul(argv[1], &end, 0);

        if (!end || *end != '\0' || parsed > UINT32_MAX) {
            fprintf(stderr, "Invalid 32-bit value: %s\n", argv[1]);
            return EXIT_FAILURE;
        }
        value = (uint32_t)parsed;
    }

    printf("Sirius OP-TEE hello example\n");
    printf("Normal world : starting Linux client application\n");

    res = TEEC_InitializeContext(NULL, &ctx);
    if (res != TEEC_SUCCESS)
        errx(EXIT_FAILURE, "TEEC_InitializeContext failed: 0x%x", res);

    printf("Normal world : OP-TEE context initialized\n");

    res = TEEC_OpenSession(&ctx, &sess, &uuid, TEEC_LOGIN_PUBLIC,
                           NULL, NULL, &err_origin);
    if (res != TEEC_SUCCESS) {
        TEEC_FinalizeContext(&ctx);
        errx(EXIT_FAILURE,
             "TEEC_OpenSession failed: 0x%x origin=0x%x", res, err_origin);
    }

    printf("Normal world : session opened with Sirius Trusted Application\n");
    printf("Normal world : sending value %" PRIu32 " to secure world\n", value);

    op.paramTypes = TEEC_PARAM_TYPES(TEEC_VALUE_INOUT,
                                     TEEC_NONE,
                                     TEEC_NONE,
                                     TEEC_NONE);
    op.params[0].value.a = value;

    res = TEEC_InvokeCommand(&sess, SIRIUS_TEE_HELLO_CMD_INCREMENT,
                             &op, &err_origin);
    if (res != TEEC_SUCCESS) {
        TEEC_CloseSession(&sess);
        TEEC_FinalizeContext(&ctx);
        errx(EXIT_FAILURE,
             "TEEC_InvokeCommand failed: 0x%x origin=0x%x", res, err_origin);
    }

    printf("Normal world : secure world returned %" PRIu32 "\n",
           op.params[0].value.a);

    if (op.params[0].value.a != value + 1U) {
        fprintf(stderr, "FAIL: expected %" PRIu32 " but received %" PRIu32 "\n",
                value + 1U, op.params[0].value.a);
        TEEC_CloseSession(&sess);
        TEEC_FinalizeContext(&ctx);
        return EXIT_FAILURE;
    }

    printf("SUCCESS      : OP-TEE round trip verified\n");

    TEEC_CloseSession(&sess);
    TEEC_FinalizeContext(&ctx);
    return EXIT_SUCCESS;
}
