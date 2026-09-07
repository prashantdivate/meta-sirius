// SPDX-License-Identifier: BSD-2-Clause
#include <tee_internal_api.h>
#include <tee_internal_api_extensions.h>

#include "sirius_tee_hello_ta.h"

TEE_Result TA_CreateEntryPoint(void)
{
    IMSG("Sirius hello TA created");
    return TEE_SUCCESS;
}

void TA_DestroyEntryPoint(void)
{
    IMSG("Sirius hello TA destroyed");
}

TEE_Result TA_OpenSessionEntryPoint(uint32_t param_types,
                                    TEE_Param params[4],
                                    void **session_context)
{
    uint32_t expected = TEE_PARAM_TYPES(TEE_PARAM_TYPE_NONE,
                                        TEE_PARAM_TYPE_NONE,
                                        TEE_PARAM_TYPE_NONE,
                                        TEE_PARAM_TYPE_NONE);

    (void)params;
    (void)session_context;

    if (param_types != expected)
        return TEE_ERROR_BAD_PARAMETERS;

    IMSG("Sirius hello TA session opened");
    return TEE_SUCCESS;
}

void TA_CloseSessionEntryPoint(void *session_context)
{
    (void)session_context;
    IMSG("Sirius hello TA session closed");
}

TEE_Result TA_InvokeCommandEntryPoint(void *session_context,
                                      uint32_t command_id,
                                      uint32_t param_types,
                                      TEE_Param params[4])
{
    uint32_t expected = TEE_PARAM_TYPES(TEE_PARAM_TYPE_VALUE_INOUT,
                                        TEE_PARAM_TYPE_NONE,
                                        TEE_PARAM_TYPE_NONE,
                                        TEE_PARAM_TYPE_NONE);

    (void)session_context;

    if (command_id != SIRIUS_TEE_HELLO_CMD_INCREMENT)
        return TEE_ERROR_NOT_SUPPORTED;

    if (param_types != expected)
        return TEE_ERROR_BAD_PARAMETERS;

    IMSG("Secure world received value %u", params[0].value.a);
    params[0].value.a++;
    IMSG("Secure world returning value %u", params[0].value.a);

    return TEE_SUCCESS;
}
