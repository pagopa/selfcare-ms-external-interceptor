env_short = "d"

tags = {
  CreatedBy   = "Terraform"
  Environment = "Dev"
  Owner       = "SelfCare"
  Source      = "https://github.com/pagopa/selfcare-ms-external-interceptor"
  CostCenter  = "TS310 - PAGAMENTI & SERVIZI"
}

container_app = {
  min_replicas = 1
  max_replicas = 1
  scale_rules  = []
  cpu          = 0.5
  memory       = "1Gi"
}

app_settings = [
  {
    name  = "INTERNAL_API_SERVICE_URL"
    value = "https://api.dev.selfcare.pagopa.it"
  },
  {
    name  = "MS_EXTERNAL_INTERCEPTOR_LOG_LEVEL"
    value = "DEBUG"
  },
  {
    name  = "SAP_ALLOWED_INSTITUTION_TYPES"
    value = "PA,GSP,SA,AS,SCP"
  },
  {
    name  = "SAP_ALLOWED_ORIGIN"
    value = "IPA,SELC"
  },
  {
    name  = "PRODUCTS_TO_RESEND"
    value = "prod-pn,prod-io-sign,prod-io-premium"
  },
  {
    name  = "JAVA_TOOL_OPTIONS"
    value = "-javaagent:applicationinsights-agent.jar"
  },
  {
    name  = "APPLICATIONINSIGHTS_ROLE_NAME"
    value = "ms-external-interceptor"
  },
  {
    name  = "KAFKA_AUTO_OFFSET_RESET_CONFIG"
    value = "earliest"
  },
  {
    name  = "ALLOWED_PRODUCER_TOPICS"
    value = "{'prod-fd': 'selfcare-fd', 'prod-fd-garantito': 'selfcare-fd'}"
  },
  {
    name  = "USERVICE_PARTY_REGISTRY_PROXY_URL"
    value = "https://selc-d-party-reg-proxy-ca.politewater-9af33050.westeurope.azurecontainerapps.io"
  },
  {
    name  = "EXTERNAL_API_BACKEND_URL"
    value = "https://selc-d-ext-api-backend-ca.politewater-9af33050.westeurope.azurecontainerapps.io"
  },
  {
    name  = "MS_CORE_URL"
    value = "https://selc-d-ms-core-ca.politewater-9af33050.westeurope.azurecontainerapps.io"
  },
  {
    name  = "KAFKA_BROKER"
    value = "selc-d-eventhub-ns.servicebus.windows.net:9093"
  },
  {
    name  = "KAFKA_SECURITY_PROTOCOL"
    value = "SASL_SSL"
  },
  {
    name  = "KAFKA_SASL_MECHANISM"
    value = "PLAIN"
  },
  {
    name  = "KAFKA_CONTRACTS_TOPIC"
    value = "SC-Contracts"
  },
  {
    name  = "KAFKA_FD_TOPIC"
    value = "Selfcare-FD"
  },
  {
    name  = "KAFKA_USERS_TOPIC"
    value = "SC-Users"
  }
]

secrets_names = {
  "APPLICATIONINSIGHTS_CONNECTION_STRING"        = "appinsights-connection-string"
  "SELFCARE_APIM_INTERNAL_API_KEY"               = "onboarding-interceptor-apim-internal"
  "MONGODB_CONNECTION_URI"                       = "mongodb-connection-string"
  "KAFKA_CONTRACTS_SELFCARE_RO_SASL_JAAS_CONFIG" = "eventhub-sc-contracts-interceptor-connection-string-lc"
  "KAFKA_USERS_SELFCARE_RO_SASL_JAAS_CONFIG"     = "eventhub-sc-users-external-interceptor-connection-string-lc"
  "KAFKA_SELFCARE_FD_WO_SASL_JAAS_CONFIG"        = "eventhub-selfcare-fd-external-interceptor-wo-connection-string-lc"
  "KAFKA_SC_CONTRACTS_SAP_WO_SASL_JAAS_CONFIG"   = "eventhub-sc-contracts-sap-external-interceptor-wo-connection-string-lc"
  "K8S_AUTHORIZATION_TOKEN"                      = "jwt-bearer-token-functions"
  "JWT_TOKEN_PUBLIC_KEY"                         = "jwt-public-key"
}
