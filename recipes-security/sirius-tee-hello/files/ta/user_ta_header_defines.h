/* SPDX-License-Identifier: BSD-2-Clause */
#ifndef USER_TA_HEADER_DEFINES_H
#define USER_TA_HEADER_DEFINES_H

#include "sirius_tee_hello_ta.h"

#define TA_UUID SIRIUS_TEE_HELLO_UUID

#define TA_FLAGS (TA_FLAG_SINGLE_INSTANCE | TA_FLAG_MULTI_SESSION)
#define TA_STACK_SIZE (2 * 1024)
#define TA_DATA_SIZE (32 * 1024)

#define TA_DESCRIPTION "Sirius OP-TEE Hello Trusted Application"
#define TA_VERSION "1.0"

#endif
