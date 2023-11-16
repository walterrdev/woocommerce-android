package com.woocommerce.android.analytics

enum class AnalyticsEvent(val siteless: Boolean = false) {
    // -- General
    APPLICATION_OPENED(siteless = true),
    APPLICATION_CLOSED(siteless = true),
    APPLICATION_INSTALLED(siteless = true),
    APPLICATION_UPGRADED(siteless = true),
    APPLICATION_VERSION_CHECK_FAILED(siteless = true),
    BACK_PRESSED(siteless = true),
    VIEW_SHOWN(siteless = true),
    APPLICATION_STORE_SNAPSHOT(siteless = false),

    // -- Login
    SIGNED_IN(siteless = true),
    ACCOUNT_LOGOUT(siteless = true),
    LOGIN_ACCESSED(siteless = true),
    LOGIN_MAGIC_LINK_EXITED(siteless = true),
    LOGIN_MAGIC_LINK_FAILED(siteless = true),
    LOGIN_MAGIC_LINK_OPENED(siteless = true),
    LOGIN_MAGIC_LINK_REQUESTED(siteless = true),
    LOGIN_MAGIC_LINK_SUCCEEDED(siteless = true),
    LOGIN_FAILED(siteless = true),
    LOGIN_INSERTED_INVALID_URL(siteless = true),
    LOGIN_AUTOFILL_CREDENTIALS_FILLED(siteless = true),
    LOGIN_AUTOFILL_CREDENTIALS_UPDATED(siteless = true),
    LOGIN_EMAIL_FORM_VIEWED(siteless = true),
    LOGIN_BY_EMAIL_HELP_FINDING_CONNECTED_EMAIL_LINK_TAPPED(siteless = true),
    LOGIN_MAGIC_LINK_OPEN_EMAIL_CLIENT_VIEWED(siteless = true),
    LOGIN_MAGIC_LINK_OPEN_EMAIL_CLIENT_CLICKED(siteless = true),
    LOGIN_MAGIC_LINK_REQUEST_FORM_VIEWED(siteless = true),
    LOGIN_PASSWORD_FORM_VIEWED(siteless = true),
    LOGIN_URL_FORM_VIEWED(siteless = true),
    LOGIN_URL_HELP_SCREEN_VIEWED(siteless = true),
    LOGIN_USERNAME_PASSWORD_FORM_VIEWED(siteless = true),
    LOGIN_TWO_FACTOR_FORM_VIEWED(siteless = true),
    LOGIN_FORGOT_PASSWORD_CLICKED(siteless = true),
    LOGIN_SOCIAL_BUTTON_CLICK(siteless = true),
    LOGIN_SOCIAL_BUTTON_FAILURE(siteless = true),
    LOGIN_SOCIAL_CONNECT_SUCCESS(siteless = true),
    LOGIN_SOCIAL_CONNECT_FAILURE(siteless = true),
    LOGIN_SOCIAL_SUCCESS(siteless = true),
    LOGIN_SOCIAL_FAILURE(siteless = true),
    LOGIN_SOCIAL_2FA_NEEDED(siteless = true),
    LOGIN_SOCIAL_ACCOUNTS_NEED_CONNECTING(siteless = true),
    LOGIN_SOCIAL_ERROR_UNKNOWN_USER(siteless = true),
    LOGIN_WPCOM_BACKGROUND_SERVICE_UPDATE(siteless = true),
    SIGNUP_EMAIL_BUTTON_TAPPED(siteless = true),
    SIGNUP_GOOGLE_BUTTON_TAPPED(siteless = true),
    SIGNUP_TERMS_OF_SERVICE_TAPPED(siteless = true),
    SIGNUP_CANCELED(siteless = true),
    SIGNUP_EMAIL_TO_LOGIN(siteless = true),
    SIGNUP_MAGIC_LINK_FAILED(siteless = true),
    SIGNUP_MAGIC_LINK_OPENED(siteless = true),
    SIGNUP_MAGIC_LINK_OPEN_EMAIL_CLIENT_CLICKED(siteless = true),
    SIGNUP_MAGIC_LINK_SENT(siteless = true),
    SIGNUP_MAGIC_LINK_SUCCEEDED(siteless = true),
    SIGNUP_SOCIAL_ACCOUNTS_NEED_CONNECTING(siteless = true),
    SIGNUP_SOCIAL_BUTTON_FAILURE(siteless = true),
    SIGNUP_SOCIAL_TO_LOGIN(siteless = true),
    ADDED_SELF_HOSTED_SITE(siteless = true),
    LOGIN_JETPACK_REQUIRED_SCREEN_VIEWED(siteless = true),
    LOGIN_WHAT_IS_JETPACK_HELP_SCREEN_VIEWED(siteless = true),
    LOGIN_WHAT_IS_JETPACK_HELP_SCREEN_LEARN_MORE_BUTTON_TAPPED(siteless = true),
    LOGIN_WHAT_IS_JETPACK_HELP_SCREEN_OK_BUTTON_TAPPED(siteless = true),
    LOGIN_SITE_ADDRESS_SITE_INFO_REQUESTED(siteless = true),
    LOGIN_SITE_ADDRESS_SITE_INFO_FAILED(siteless = true),
    LOGIN_SITE_ADDRESS_SITE_INFO_SUCCEEDED(siteless = true),
    LOGIN_FIND_CONNECTED_EMAIL_HELP_SCREEN_VIEWED(siteless = true),
    LOGIN_FIND_CONNECTED_EMAIL_HELP_SCREEN_NEED_MORE_HELP_LINK_TAPPED(siteless = true),
    LOGIN_FIND_CONNECTED_EMAIL_HELP_SCREEN_OK_BUTTON_TAPPED(siteless = true),
    LOGIN_NO_JETPACK_SCREEN_VIEWED(siteless = true),
    LOGIN_NO_JETPACK_LOGOUT_LINK_TAPPED(siteless = true),
    LOGIN_NO_JETPACK_TRY_AGAIN_TAPPED(siteless = true),
    LOGIN_NO_JETPACK_MENU_HELP_TAPPED(siteless = true),
    LOGIN_NO_JETPACK_WHAT_IS_JETPACK_LINK_TAPPED(siteless = true),
    LOGIN_DISCOVERY_ERROR_SCREEN_VIEWED(siteless = true),
    LOGIN_DISCOVERY_ERROR_TROUBLESHOOT_BUTTON_TAPPED(siteless = true),
    LOGIN_DISCOVERY_ERROR_TRY_AGAIN_TAPPED(siteless = true),
    LOGIN_DISCOVERY_ERROR_SIGN_IN_WORDPRESS_BUTTON_TAPPED(siteless = true),
    LOGIN_DISCOVERY_ERROR_MENU_HELP_TAPPED(siteless = true),
    LOGIN_MAGIC_LINK_INTERCEPT_SCREEN_VIEWED(siteless = true),
    LOGIN_MAGIC_LINK_INTERCEPT_RETRY_TAPPED(siteless = true),
    LOGIN_MAGIC_LINK_UPDATE_TOKEN_FAILED(siteless = true),
    LOGIN_MAGIC_LINK_FETCH_ACCOUNT_FAILED(siteless = true),
    LOGIN_MAGIC_LINK_FETCH_ACCOUNT_SETTINGS_FAILED(siteless = true),
    LOGIN_MAGIC_LINK_FETCH_SITES_FAILED(siteless = true),
    LOGIN_MAGIC_LINK_FETCH_ACCOUNT_SUCCESS(siteless = true),
    LOGIN_MAGIC_LINK_FETCH_ACCOUNT_SETTINGS_SUCCESS(siteless = true),
    LOGIN_MAGIC_LINK_FETCH_SITES_SUCCESS(siteless = true),
    UNIFIED_LOGIN_STEP(siteless = true),
    UNIFIED_LOGIN_FAILURE(siteless = true),
    UNIFIED_LOGIN_INTERACTION(siteless = true),
    LOGIN_JETPACK_SETUP_BUTTON_TAPPED(siteless = true),
    LOGIN_JETPACK_SETUP_DISMISSED(siteless = true),
    LOGIN_JETPACK_SETUP_COMPLETED(siteless = true),
    LOGIN_JETPACK_CONNECTION_ERROR_SHOWN(siteless = true),
    LOGIN_JETPACK_CONNECTION_URL_FETCH_FAILED(siteless = true),
    LOGIN_JETPACK_CONNECT_BUTTON_TAPPED(siteless = true),
    LOGIN_JETPACK_CONNECT_COMPLETED(siteless = true),
    LOGIN_JETPACK_CONNECT_DISMISSED(siteless = true),
    LOGIN_JETPACK_CONNECTION_VERIFICATION_FAILED(siteless = true),
    LOGIN_WITH_QR_CODE_BUTTON_TAPPED(siteless = true),
    LOGIN_WITH_QR_CODE_SCANNED(siteless = true),
    LOGIN_PROLOGUE_CREATE_SITE_TAPPED(siteless = true),
    LOGIN_MALFORMED_APP_LOGIN_LINK(siteless = true),
    LOGIN_APP_LOGIN_LINK_SUCCESS(siteless = true),
    SIGNUP_LOGIN_BUTTON_TAPPED(siteless = true),
    SIGNUP_SUBMITTED(siteless = true),
    SIGNUP_SUCCESS(siteless = true),
    SIGNUP_ERROR(siteless = true),
    LOGIN_SITE_CREDENTIALS_LOGIN_FAILED(siteless = true),
    LOGIN_INSUFFICIENT_ROLE(siteless = false),
    LOGIN_2FA_NEEDED(siteless = true),
    LOGIN_USE_SECURITY_KEY_CLICKED(siteless = true),
    LOGIN_SECURITY_KEY_FAILURE(siteless = true),
    LOGIN_SECURITY_KEY_SUCCESS(siteless = true),

    // -- Site Picker
    SITE_PICKER_STORES_SHOWN(siteless = true),
    SITE_PICKER_CONTINUE_TAPPED(siteless = true),
    SITE_PICKER_HELP_BUTTON_TAPPED(siteless = true),
    SITE_PICKER_AUTO_LOGIN_SUBMITTED(siteless = true),
    SITE_PICKER_AUTO_LOGIN_ERROR_NOT_CONNECTED_TO_USER(siteless = true),
    SITE_PICKER_AUTO_LOGIN_ERROR_NOT_WOO_STORE(siteless = true),
    SITE_PICKER_VIEW_CONNECTED_STORES_BUTTON_TAPPED(siteless = true),
    SITE_PICKER_HELP_FINDING_CONNECTED_EMAIL_LINK_TAPPED(siteless = true),
    SITE_PICKER_NOT_CONNECTED_JETPACK_REFRESH_APP_LINK_TAPPED(siteless = true),
    SITE_PICKER_NON_WOO_SITE_TAPPED(siteless = true),
    SITE_PICKER_NEW_TO_WOO_TAPPED(siteless = true),
    SITE_PICKER_ADD_A_STORE_TAPPED(siteless = true),
    SITE_PICKER_CONNECT_EXISTING_STORE_TAPPED(siteless = true),
    SITE_PICKER_SITE_DISCOVERY(siteless = true),
    SITE_PICKER_JETPACK_TIMEOUT_ERROR_SHOWN(siteless = true),
    SITE_PICKER_JETPACK_TIMEOUT_CONTACT_SUPPORT_CLICKED(siteless = true),
    SITE_PICKER_CREATE_SITE_TAPPED(siteless = true),
    LOGIN_WOOCOMMERCE_SITE_CREATED,

    // -- Jetpack Installation for Login
    LOGIN_JETPACK_SITE_CREDENTIAL_SCREEN_VIEWED(siteless = true),
    LOGIN_JETPACK_SITE_CREDENTIAL_SCREEN_DISMISSED(siteless = true),
    LOGIN_JETPACK_SITE_CREDENTIAL_INSTALL_BUTTON_TAPPED(siteless = true),
    LOGIN_JETPACK_SITE_CREDENTIAL_RESET_PASSWORD_BUTTON_TAPPED(siteless = true),
    LOGIN_JETPACK_SITE_CREDENTIAL_DID_SHOW_ERROR_ALERT(siteless = true),
    LOGIN_JETPACK_SITE_CREDENTIAL_DID_FINISH_LOGIN(siteless = true),
    LOGIN_JETPACK_SETUP_SCREEN_VIEWED(siteless = true),
    LOGIN_JETPACK_SETUP_SCREEN_DISMISSED(siteless = true),
    LOGIN_JETPACK_SETUP_INSTALL_SUCCESSFUL(siteless = true),
    LOGIN_JETPACK_SETUP_INSTALL_FAILED(siteless = true),
    LOGIN_JETPACK_SETUP_ACTIVATION_SUCCESSFUL(siteless = true),
    LOGIN_JETPACK_SETUP_ACTIVATION_FAILED(siteless = true),
    LOGIN_JETPACK_SETUP_FETCH_JETPACK_CONNECTION_URL_SUCCESSFUL(siteless = true),
    LOGIN_JETPACK_SETUP_FETCH_JETPACK_CONNECTION_URL_FAILED(siteless = true),
    LOGIN_JETPACK_SETUP_CANNOT_FIND_WPCOM_USER(siteless = true),
    LOGIN_JETPACK_SETUP_AUTHORIZED_USING_DIFFERENT_WPCOM_ACCOUNT(siteless = true),
    LOGIN_JETPACK_SETUP_ALL_STEPS_MARKED_DONE(siteless = true),
    LOGIN_JETPACK_SETUP_ERROR_CHECKING_JETPACK_CONNECTION(siteless = true),
    LOGIN_JETPACK_SETUP_GO_TO_STORE_BUTTON_TAPPED(siteless = true),
    LOGIN_JETPACK_FETCHING_WPCOM_SITES_FAILED(siteless = true),
    LOGIN_JETPACK_SETUP_GET_SUPPORT_BUTTON_TAPPED(siteless = true),
    LOGIN_JETPACK_SETUP_TRY_AGAIN_BUTTON_TAPPED(siteless = true),

    // -- Dashboard
    DASHBOARD_PULLED_TO_REFRESH,
    DASHBOARD_SHARE_YOUR_STORE_BUTTON_TAPPED,
    DASHBOARD_MAIN_STATS_DATE,
    DASHBOARD_MAIN_STATS_LOADED,
    DASHBOARD_TOP_PERFORMERS_DATE,
    DASHBOARD_TOP_PERFORMERS_LOADED,
    DASHBOARD_NEW_STATS_REVERTED_BANNER_DISMISS_TAPPED,
    DASHBOARD_NEW_STATS_REVERTED_BANNER_LEARN_MORE_TAPPED,
    DASHBOARD_WAITING_TIME_LOADED,
    DASHBOARD_SEE_MORE_ANALYTICS_TAPPED,
    DASHBOARD_STORE_TIMEZONE_DIFFER_FROM_DEVICE,
    USED_ANALYTICS,

    // -- Analytics Hub
    ANALYTICS_HUB_DATE_RANGE_BUTTON_TAPPED,
    ANALYTICS_HUB_DATE_RANGE_SELECTED,
    ANALYTICS_HUB_PULL_TO_REFRESH_TRIGGERED,

    // -- Orders List
    ORDERS_LIST_FILTER,
    ORDERS_LIST_SEARCH,
    ORDERS_LIST_LOADED,
    ORDER_LIST_LOAD_ERROR,
    ORDERS_LIST_PULLED_TO_REFRESH,
    ORDERS_LIST_MENU_SEARCH_TAPPED,
    ORDERS_LIST_VIEW_FILTER_OPTIONS_TAPPED,
    ORDER_LIST_WAITING_TIME_LOADED,
    ORDER_LIST_PRODUCT_BARCODE_SCANNING_TAPPED,
    ORDER_LIST_TEST_ORDER_DISPLAYED,
    ORDER_LIST_TRY_TEST_ORDER_TAPPED,
    TEST_ORDER_START_TAPPED,

    // -- IPP feedback banner
    IPP_FEEDBACK_BANNER_SHOWN,
    IPP_FEEDBACK_BANNER_DISMISSED,
    IPP_FEEDBACK_BANNER_CTA_TAPPED,

    FILTER_ORDERS_BY_STATUS_DIALOG_OPTION_SELECTED,
    ORDER_FILTER_LIST_CLEAR_MENU_BUTTON_TAPPED,

    // -- Payments
    PAYMENTS_FLOW_ORDER_COLLECT_PAYMENT_TAPPED,
    PAYMENTS_FLOW_COMPLETED,
    PAYMENTS_FLOW_COLLECT,
    PAYMENTS_FLOW_FAILED,

    // -- Upsell Banner
    FEATURE_CARD_SHOWN,
    FEATURE_CARD_DISMISSED,
    FEATURE_CARD_CTA_TAPPED,

    // -- Just In Time Messages
    JITM_FETCH_SUCCESS,
    JITM_FETCH_FAILURE,
    JITM_DISPLAYED,
    JITM_CTA_TAPPED,
    JITM_DISMISS_TAPPED,
    JITM_DISMISS_SUCCESS,
    JITM_DISMISS_FAILURE,

    PAYMENTS_FLOW_CANCELED,
    SIMPLE_PAYMENTS_FLOW_NOTE_ADDED,
    SIMPLE_PAYMENTS_FLOW_TAXES_TOGGLED,

    // -- Order Detail
    ORDER_OPEN,
    ORDER_CONTACT_ACTION,
    ORDER_CONTACT_ACTION_FAILED,
    ORDER_STATUS_CHANGE,
    ORDER_STATUS_CHANGE_FAILED,
    ORDER_STATUS_CHANGE_SUCCESS,
    ORDER_DETAIL_PULLED_TO_REFRESH,
    ORDER_DETAIL_ADD_NOTE_BUTTON_TAPPED,
    ORDER_DETAIL_CUSTOMER_INFO_SHOW_BILLING_TAPPED,
    ORDER_DETAIL_CUSTOMER_INFO_HIDE_BILLING_TAPPED,
    ORDER_DETAIL_CUSTOMER_INFO_EMAIL_MENU_EMAIL_TAPPED,
    ORDER_DETAIL_CUSTOMER_INFO_PHONE_MENU_PHONE_TAPPED,
    ORDER_DETAIL_CUSTOMER_INFO_PHONE_MENU_SMS_TAPPED,
    ORDER_DETAIL_FULFILL_ORDER_BUTTON_TAPPED,
    ORDER_DETAIL_PRODUCT_TAPPED,
    ORDER_DETAIL_CREATE_SHIPPING_LABEL_BUTTON_TAPPED,
    ORDER_DETAIL_WAITING_TIME_LOADED,
    ORDER_VIEW_CUSTOM_FIELDS_TAPPED,
    ORDER_DETAILS_SUBSCRIPTIONS_SHOWN,
    ORDER_DETAILS_GIFT_CARD_SHOWN,
    ORDER_PRODUCTS_LOADED,

    // - Order detail editing
    ORDER_DETAIL_EDIT_FLOW_STARTED,
    ORDER_DETAIL_EDIT_FLOW_COMPLETED,
    ORDER_DETAIL_EDIT_FLOW_FAILED,
    ORDER_DETAIL_EDIT_FLOW_CANCELED,
    ORDER_EDIT_BUTTON_TAPPED,
    PLUGINS_NOT_SYNCED_YET,

    // -- Order Creation
    ORDERS_ADD_NEW,
    ORDER_PRODUCT_ADD,
    ORDER_CUSTOMER_ADD,
    ORDER_CUSTOMER_DELETE,
    ORDER_FEE_ADD,
    ORDER_SHIPPING_METHOD_ADD,
    ORDER_CREATE_BUTTON_TAPPED,
    ORDER_CREATION_SUCCESS,
    ORDER_CREATION_FAILED,
    ORDER_SYNC_FAILED,
    ORDER_CREATION_CUSTOMER_SEARCH,
    ORDER_CREATION_CUSTOMER_ADDED,
    ORDER_CREATION_CUSTOMER_ADD_MANUALLY_TAPPED,
    ORDER_PRODUCT_QUANTITY_CHANGE,
    ORDER_PRODUCT_REMOVE,
    ORDER_FEE_REMOVE,
    ORDER_SHIPPING_METHOD_REMOVE,
    ORDER_CREATION_PRODUCT_SELECTOR_ITEM_SELECTED,
    ORDER_CREATION_PRODUCT_SELECTOR_ITEM_UNSELECTED,
    ORDER_CREATION_PRODUCT_SELECTOR_CONFIRM_BUTTON_TAPPED,
    ORDER_CREATION_PRODUCT_SELECTOR_CLEAR_SELECTION_BUTTON_TAPPED,
    ORDER_CREATION_PRODUCT_BARCODE_SCANNING_TAPPED,
    ORDER_CREATION_PRODUCT_SELECTOR_SEARCH_TRIGGERED,
    ORDER_TAXES_HELP_BUTTON_TAPPED,
    TAX_EDUCATIONAL_DIALOG_EDIT_IN_ADMIN_BUTTON_TAPPED,
    ORDER_CREATION_SET_NEW_TAX_RATE_TAPPED,
    TAX_RATE_SELECTOR_TAX_RATE_TAPPED,
    TAX_RATE_SELECTOR_EDIT_IN_ADMIN_TAPPED,
    TAX_RATE_AUTO_TAX_BOTTOM_SHEET_DISPLAYED,
    TAX_RATE_AUTO_TAX_RATE_SET_NEW_RATE_FOR_ORDER_TAPPED,
    TAX_RATE_AUTO_TAX_RATE_CLEAR_ADDRESS_TAPPED,

    // -- Custom Amounts
    ORDER_CREATION_ADD_CUSTOM_AMOUNT_TAPPED,
    ORDER_CREATION_EDIT_CUSTOM_AMOUNT_TAPPED,
    ORDER_CREATION_REMOVE_CUSTOM_AMOUNT_TAPPED,
    ADD_CUSTOM_AMOUNT_NAME_ADDED,
    ADD_CUSTOM_AMOUNT_DONE_TAPPED,

    // -- Barcode Scanner
    BARCODE_SCANNING_SUCCESS,
    BARCODE_SCANNING_FAILURE,

    // -- Product Search Via SKU
    PRODUCT_SEARCH_VIA_SKU_SUCCESS,
    PRODUCT_SEARCH_VIA_SKU_FAILURE,

    // -- Refunds
    CREATE_ORDER_REFUND_NEXT_BUTTON_TAPPED,
    CREATE_ORDER_REFUND_TAB_CHANGED,
    CREATE_ORDER_REFUND_SELECT_ALL_ITEMS_BUTTON_TAPPED,
    CREATE_ORDER_REFUND_ITEM_QUANTITY_DIALOG_OPENED,
    CREATE_ORDER_REFUND_PRODUCT_AMOUNT_DIALOG_OPENED,
    CREATE_ORDER_REFUND_SUMMARY_REFUND_BUTTON_TAPPED,
    REFUND_CREATE,
    REFUND_CREATE_SUCCESS,
    REFUND_CREATE_FAILED,

    // -- Order Notes
    ADD_ORDER_NOTE_ADD_BUTTON_TAPPED,
    ADD_ORDER_NOTE_EMAIL_NOTE_TO_CUSTOMER_TOGGLED,
    ORDER_NOTE_ADD,
    ORDER_NOTE_ADD_FAILED,
    ORDER_NOTE_ADD_SUCCESS,

    // -- Order Shipment Tracking
    ORDER_SHIPMENT_TRACKING_CARRIER_SELECTED,
    ORDER_TRACKING_ADD,
    ORDER_TRACKING_ADD_FAILED,
    ORDER_TRACKING_ADD_SUCCESS,
    ORDER_SHIPMENT_TRACKING_ADD_BUTTON_TAPPED,
    ORDER_SHIPMENT_TRACKING_CUSTOM_PROVIDER_SELECTED,
    ORDER_TRACKING_DELETE_SUCCESS,
    ORDER_TRACKING_DELETE_FAILED,
    ORDER_TRACKING_PROVIDERS_LOADED,
    SHIPMENT_TRACKING_MENU_ACTION,

    // -- Order Coupon
    ORDER_COUPON_ADD,
    ORDER_COUPON_REMOVE,
    ORDER_GO_TO_COUPON_BUTTON_TAPPED,

    // -- Order discount
    ORDER_PRODUCT_DISCOUNT_ADD,
    ORDER_PRODUCT_DISCOUNT_REMOVE,
    ORDER_PRODUCT_DISCOUNT_ADD_BUTTON_TAPPED,
    ORDER_PRODUCT_DISCOUNT_EDIT_BUTTON_TAPPED,

    // -- Shipping Labels
    SHIPPING_LABEL_API_REQUEST,
    SHIPPING_LABEL_PRINT_REQUESTED,
    SHIPPING_LABEL_REFUND_REQUESTED,
    SHIPPING_LABEL_PURCHASE_FLOW,
    SHIPPING_LABEL_DISCOUNT_INFO_BUTTON_TAPPED,
    SHIPPING_LABEL_EDIT_ADDRESS_DONE_BUTTON_TAPPED,
    SHIPPING_LABEL_EDIT_ADDRESS_USE_ADDRESS_AS_IS_BUTTON_TAPPED,
    SHIPPING_LABEL_EDIT_ADDRESS_OPEN_MAP_BUTTON_TAPPED,
    SHIPPING_LABEL_EDIT_ADDRESS_CONTACT_CUSTOMER_BUTTON_TAPPED,
    SHIPPING_LABEL_ADDRESS_SUGGESTIONS_USE_SELECTED_ADDRESS_BUTTON_TAPPED,
    SHIPPING_LABEL_ADDRESS_SUGGESTIONS_EDIT_SELECTED_ADDRESS_BUTTON_TAPPED,
    SHIPPING_LABEL_ADDRESS_VALIDATION_FAILED,
    SHIPPING_LABEL_ADDRESS_VALIDATION_SUCCEEDED,
    SHIPPING_LABEL_ORDER_FULFILL_SUCCEEDED,
    SHIPPING_LABEL_ORDER_FULFILL_FAILED,
    SHIPPING_LABEL_MOVE_ITEM_TAPPED,
    SHIPPING_LABEL_ITEM_MOVED,
    SHIPPING_LABEL_ADD_PAYMENT_METHOD_TAPPED,
    SHIPPING_LABEL_PAYMENT_METHOD_ADDED,
    SHIPPING_LABEL_ADD_PACKAGE_TAPPED,
    SHIPPING_LABEL_PACKAGE_ADDED_SUCCESSFULLY,
    SHIPPING_LABEL_ADD_PACKAGE_FAILED,
    SHIPPING_LABEL_ORDER_IS_ELIGIBLE,

    // -- Card Present Payments - onboarding
    CARD_PRESENT_ONBOARDING_LEARN_MORE_TAPPED,
    CARD_PRESENT_ONBOARDING_NOT_COMPLETED,
    CARD_PRESENT_ONBOARDING_COMPLETED,
    CARD_PRESENT_ONBOARDING_STEP_SKIPPED,
    CARD_PRESENT_ONBOARDING_CTA_TAPPED,
    CARD_PRESENT_ONBOARDING_CTA_FAILED,
    CARD_PRESENT_PAYMENT_GATEWAY_SELECTED,

    // -- Cash on Delivery - onboarding
    ENABLE_CASH_ON_DELIVERY_SUCCESS,
    ENABLE_CASH_ON_DELIVERY_FAILED,
    DISABLE_CASH_ON_DELIVERY_SUCCESS,
    DISABLE_CASH_ON_DELIVERY_FAILED,

    // -- Card Present Payments - collection
    CARD_PRESENT_COLLECT_PAYMENT_FAILED,
    CARD_PRESENT_COLLECT_PAYMENT_CANCELLED,
    CARD_PRESENT_COLLECT_PAYMENT_SUCCESS,
    CARD_PRESENT_PAYMENT_FAILED_CONTACT_SUPPORT_TAPPED,
    CARD_PRESENT_TAP_TO_PAY_PAYMENT_FAILED_ENABLE_NFC_TAPPED,

    // --Card Present Payments - Interac refund
    CARD_PRESENT_COLLECT_INTERAC_PAYMENT_SUCCESS,
    CARD_PRESENT_COLLECT_INTERAC_PAYMENT_FAILED,
    CARD_PRESENT_COLLECT_INTERAC_REFUND_CANCELLED,

    // -- Card Reader - discovery
    CARD_READER_DISCOVERY_TAPPED,
    CARD_READER_DISCOVERY_FAILED,
    CARD_READER_DISCOVERY_READER_DISCOVERED,

    // -- Card Reader - connection
    CARD_READER_CONNECTION_TAPPED,
    CARD_READER_CONNECTION_FAILED,
    CARD_READER_CONNECTION_SUCCESS,
    CARD_READER_DISCONNECT_TAPPED,
    CARD_READER_AUTO_CONNECTION_STARTED,
    CARD_PRESENT_CONNECTION_LEARN_MORE_TAPPED,
    MANAGE_CARD_READERS_AUTOMATIC_DISCONNECT_BUILT_IN_READER,
    CARD_READER_AUTOMATIC_DISCONNECT,

    // -- Card Reader - software update
    CARD_READER_SOFTWARE_UPDATE_STARTED,
    CARD_READER_SOFTWARE_UPDATE_SUCCESS,
    CARD_READER_SOFTWARE_UPDATE_FAILED,
    CARD_READER_SOFTWARE_UPDATE_ALERT_SHOWN,
    CARD_READER_SOFTWARE_UPDATE_ALERT_INSTALL_CLICKED,

    // -- Card Reader - Location
    CARD_READER_LOCATION_SUCCESS,
    CARD_READER_LOCATION_FAILURE,
    CARD_READER_LOCATION_MISSING_TAPPED,

    // -- Card Reader - reader type selection
    CARD_PRESENT_SELECT_READER_TYPE_BUILT_IN_TAPPED,
    CARD_PRESENT_SELECT_READER_TYPE_BLUETOOTH_TAPPED,

    // -- Card Reader - tap to pay not available
    CARD_PRESENT_TAP_TO_PAY_NOT_AVAILABLE,

    // -- Receipts
    RECEIPT_PRINT_TAPPED,
    RECEIPT_EMAIL_TAPPED,
    RECEIPT_EMAIL_FAILED,
    RECEIPT_PRINT_FAILED,
    RECEIPT_PRINT_CANCELED,
    RECEIPT_PRINT_SUCCESS,
    RECEIPT_VIEW_TAPPED,

    // -- Top-level navigation
    MAIN_MENU_SETTINGS_TAPPED,
    MAIN_MENU_CONTACT_SUPPORT_TAPPED,
    MAIN_TAB_DASHBOARD_SELECTED,
    MAIN_TAB_DASHBOARD_RESELECTED,
    MAIN_TAB_ORDERS_SELECTED,
    MAIN_TAB_ORDERS_RESELECTED,
    MAIN_TAB_PRODUCTS_SELECTED,
    MAIN_TAB_PRODUCTS_RESELECTED,
    MAIN_TAB_HUB_MENU_SELECTED,
    MAIN_TAB_HUB_MENU_RESELECTED,

    // -- Settings
    SETTING_CHANGE,
    SETTING_CHANGE_FAILED,
    SETTING_CHANGE_SUCCESS,
    SETTINGS_LOGOUT_BUTTON_TAPPED,
    SETTINGS_LOGOUT_CONFIRMATION_DIALOG_RESULT,
    SETTINGS_BETA_FEATURES_BUTTON_TAPPED,
    SETTINGS_PRIVACY_SETTINGS_BUTTON_TAPPED,
    SETTINGS_FEATURE_REQUEST_BUTTON_TAPPED,
    SETTINGS_ABOUT_WOOCOMMERCE_LINK_TAPPED,
    SETTINGS_ABOUT_BUTTON_TAPPED,
    SETTINGS_ABOUT_OPEN_SOURCE_LICENSES_LINK_TAPPED,
    SETTINGS_NOTIFICATIONS_OPEN_CHANNEL_SETTINGS_BUTTON_TAPPED,
    SETTINGS_WE_ARE_HIRING_BUTTON_TAPPED,
    SETTINGS_IMAGE_OPTIMIZATION_TOGGLED,
    SETTINGS_CARD_PRESENT_SELECT_PAYMENT_GATEWAY_TAPPED,
    PRIVACY_SETTINGS_PRIVACY_POLICY_LINK_TAPPED,
    PRIVACY_SETTINGS_SHARE_INFO_LINK_TAPPED,
    PRIVACY_SETTINGS_THIRD_PARTY_TRACKING_INFO_LINK_TAPPED,
    SETTINGS_DOMAINS_TAPPED,

    // -- Payments Hub
    PAYMENTS_HUB_COLLECT_PAYMENT_TAPPED,
    PAYMENTS_HUB_ORDER_CARD_READER_TAPPED,
    PAYMENTS_HUB_CARD_READER_MANUALS_TAPPED,
    PAYMENTS_HUB_MANAGE_CARD_READERS_TAPPED,
    PAYMENTS_HUB_ONBOARDING_ERROR_TAPPED,
    PAYMENTS_HUB_CASH_ON_DELIVERY_TOGGLED,
    PAYMENTS_HUB_CASH_ON_DELIVERY_TOGGLED_LEARN_MORE_TAPPED,
    IN_PERSON_PAYMENTS_LEARN_MORE_TAPPED,
    PAYMENTS_HUB_TAP_TO_PAY_TAPPED,
    PAYMENTS_HUB_TAP_TO_PAY_FEEDBACK_TAPPED,
    PAYMENTS_HUB_TAP_TO_PAY_ABOUT_TAPPED,

    // -- TAP TO PAY SUMMARY
    TAP_TO_PAY_SUMMARY_TRY_PAYMENT_TAPPED,
    TAP_TO_PAY_SUMMARY_SHOWN,
    CARD_PRESENT_TAP_TO_PAY_TEST_PAYMENT_REFUND_SUCCESS,
    CARD_PRESENT_TAP_TO_PAY_TEST_PAYMENT_REFUND_FAILED,

    // -- Product list
    PRODUCT_LIST_LOADED,
    PRODUCT_LIST_LOAD_ERROR,
    PRODUCT_LIST_PRODUCT_TAPPED,
    PRODUCT_LIST_PULLED_TO_REFRESH,
    PRODUCT_LIST_SEARCHED,
    PRODUCT_LIST_MENU_SEARCH_TAPPED,
    PRODUCT_LIST_VIEW_FILTER_OPTIONS_TAPPED,
    PRODUCT_LIST_VIEW_SORTING_OPTIONS_TAPPED,
    PRODUCT_LIST_SORTING_OPTION_SELECTED,
    PRODUCT_LIST_ADD_PRODUCT_BUTTON_TAPPED,
    ADD_PRODUCT_PRODUCT_TYPE_SELECTED,
    PRODUCT_LIST_BULK_UPDATE_REQUESTED,
    PRODUCT_LIST_BULK_UPDATE_CONFIRMED,
    PRODUCT_LIST_BULK_UPDATE_SUCCESS,
    PRODUCT_LIST_BULK_UPDATE_FAILURE,
    PRODUCT_LIST_BULK_UPDATE_SELECT_ALL_TAPPED,

    // -- Product detail
    PRODUCT_DETAIL_LOADED,
    PRODUCT_DETAIL_IMAGE_TAPPED,
    PRODUCT_DETAIL_SHARE_BUTTON_TAPPED,
    PRODUCT_DETAIL_UPDATE_BUTTON_TAPPED,
    PRODUCT_DETAIL_VIEW_EXTERNAL_TAPPED,
    PRODUCT_DETAIL_VIEW_PRODUCT_VARIANTS_TAPPED,
    PRODUCT_DETAIL_VIEW_PRODUCT_DESCRIPTION_TAPPED,
    PRODUCT_DETAIL_VIEW_PRICE_SETTINGS_TAPPED,
    PRODUCT_DETAIL_VIEW_INVENTORY_SETTINGS_TAPPED,
    PRODUCT_DETAIL_VIEW_SHIPPING_SETTINGS_TAPPED,
    PRODUCT_DETAIL_VIEW_SHORT_DESCRIPTION_TAPPED,
    PRODUCT_DETAIL_VIEW_CATEGORIES_TAPPED,
    PRODUCT_DETAIL_VIEW_TAGS_TAPPED,
    PRODUCT_DETAIL_VIEW_PRODUCT_TYPE_TAPPED,
    PRODUCT_DETAIL_VIEW_PRODUCT_REVIEWS_TAPPED,
    PRODUCT_DETAIL_VIEW_GROUPED_PRODUCTS_TAPPED,
    PRODUCT_DETAIL_VIEW_LINKED_PRODUCTS_TAPPED,
    PRODUCT_DETAIL_VIEW_DOWNLOADABLE_FILES_TAPPED,
    PRODUCT_PRICE_SETTINGS_DONE_BUTTON_TAPPED,
    PRODUCT_INVENTORY_SETTINGS_DONE_BUTTON_TAPPED,
    PRODUCT_SHIPPING_SETTINGS_DONE_BUTTON_TAPPED,
    PRODUCT_IMAGE_SETTINGS_DONE_BUTTON_TAPPED,
    PRODUCT_CATEGORY_SETTINGS_DONE_BUTTON_TAPPED,
    PRODUCT_TAG_SETTINGS_DONE_BUTTON_TAPPED,
    PRODUCT_DETAIL_UPDATE_SUCCESS,
    PRODUCT_DETAIL_UPDATE_ERROR,
    ADD_PRODUCT_PUBLISH_TAPPED,
    ADD_PRODUCT_SAVE_AS_DRAFT_TAPPED,
    ADD_PRODUCT_SUCCESS,
    ADD_PRODUCT_FAILED,
    PRODUCT_IMAGE_UPLOAD_FAILED,
    PRODUCT_DETAIL_PRODUCT_DELETED,
    FIRST_CREATED_PRODUCT_SHOWN,
    FIRST_CREATED_PRODUCT_SHARE_TAPPED,

    // -- Product Categories
    PRODUCT_CATEGORIES_LOADED,
    PRODUCT_CATEGORIES_LOAD_FAILED,
    PRODUCT_CATEGORIES_PULLED_TO_REFRESH,
    PRODUCT_CATEGORY_SETTINGS_ADD_BUTTON_TAPPED,

    // -- Add Product Category
    PARENT_CATEGORIES_LOADED,
    PARENT_CATEGORIES_LOAD_FAILED,
    PARENT_CATEGORIES_PULLED_TO_REFRESH,
    ADD_PRODUCT_CATEGORY_SAVE_TAPPED,

    // -- Product Tags
    PRODUCT_TAGS_LOADED,
    PRODUCT_TAGS_LOAD_FAILED,
    PRODUCT_TAGS_PULLED_TO_REFRESH,

    // -- Product reviews
    PRODUCT_REVIEWS_LOADED,
    PRODUCT_REVIEWS_LOAD_FAILED,
    PRODUCT_REVIEWS_PULLED_TO_REFRESH,
    REVIEW_REPLY_SEND,
    REVIEW_REPLY_SEND_SUCCESS,
    REVIEW_REPLY_SEND_FAILED,

    PRODUCTS_DOWNLOADABLE_FILE,

    // -- Linked Products
    LINKED_PRODUCTS,

    // -- Connected Products (Grouped products, Upsells, Cross-sells)
    CONNECTED_PRODUCTS_LIST,

    // -- Product external link
    PRODUCT_DETAIL_VIEW_EXTERNAL_PRODUCT_LINK_TAPPED,
    EXTERNAL_PRODUCT_LINK_SETTINGS_DONE_BUTTON_TAPPED,

    // -- Product subscriptions
    PRODUCT_DETAILS_VIEW_SUBSCRIPTIONS_TAPPED,
    PRODUCT_VARIATION_VIEW_SUBSCRIPTIONS_TAPPED,

    // -- Product attributes
    PRODUCT_ATTRIBUTE_EDIT_BUTTON_TAPPED,
    PRODUCT_ATTRIBUTE_ADD_BUTTON_TAPPED,
    PRODUCT_ATTRIBUTE_UPDATED,
    PRODUCT_ATTRIBUTE_UPDATE_SUCCESS,
    PRODUCT_ATTRIBUTE_UPDATE_FAILED,
    PRODUCT_ATTRIBUTE_RENAME_BUTTON_TAPPED,
    PRODUCT_ATTRIBUTE_REMOVE_BUTTON_TAPPED,
    PRODUCT_ATTRIBUTE_OPTIONS_ROW_TAPPED,

    // -- Product variation
    PRODUCT_VARIATION_VIEW_VARIATION_DESCRIPTION_TAPPED,
    PRODUCT_VARIATION_VIEW_PRICE_SETTINGS_TAPPED,
    PRODUCT_VARIATION_VIEW_INVENTORY_SETTINGS_TAPPED,
    PRODUCT_VARIATION_VIEW_SHIPPING_SETTINGS_TAPPED,
    PRODUCT_VARIATION_VIEW_VARIATION_DETAIL_TAPPED,
    PRODUCT_VARIATION_VIEW_VARIATION_VISIBILITY_SWITCH_TAPPED,
    PRODUCT_VARIATION_IMAGE_TAPPED,
    PRODUCT_VARIATION_UPDATE_BUTTON_TAPPED,
    PRODUCT_VARIATION_UPDATE_SUCCESS,
    PRODUCT_VARIATION_UPDATE_ERROR,
    PRODUCT_VARIATION_LOADED,
    PRODUCT_VARIATION_ADD_FIRST_TAPPED,
    PRODUCT_VARIATION_ADD_MORE_TAPPED,
    PRODUCT_VARIATION_CREATION_SUCCESS,
    PRODUCT_VARIATION_CREATION_FAILED,
    PRODUCT_VARIATION_REMOVE_BUTTON_TAPPED,
    PRODUCT_VARIATION_EDIT_ATTRIBUTE_DONE_BUTTON_TAPPED,
    PRODUCT_VARIATION_EDIT_ATTRIBUTE_OPTIONS_DONE_BUTTON_TAPPED,
    PRODUCT_VARIATION_ATTRIBUTE_ADDED_BACK_BUTTON_TAPPED,
    PRODUCT_VARIATION_DETAILS_ATTRIBUTES_TAPPED,
    PRODUCT_VARIATION_GENERATION_REQUESTED,
    PRODUCT_VARIATION_GENERATION_LIMIT_REACHED,
    PRODUCT_VARIATION_GENERATION_CONFIRMED,
    PRODUCT_VARIATION_GENERATION_SUCCESS,
    PRODUCT_VARIATION_GENERATION_FAILURE,

    // -- Product Add-ons
    PRODUCT_ADDONS_BETA_FEATURES_SWITCH_TOGGLED,
    PRODUCT_ADDONS_ORDER_ADDONS_VIEWED,
    PRODUCT_ADDONS_PRODUCT_DETAIL_VIEW_PRODUCT_ADDONS_TAPPED,
    PRODUCT_ADDONS_ORDER_DETAIL_VIEW_PRODUCT_ADDONS_TAPPED,
    PRODUCT_ADDONS_REFUND_DETAIL_VIEW_PRODUCT_ADDONS_TAPPED,

    PRODUCT_DETAIL_ADD_IMAGE_TAPPED,
    PRODUCT_IMAGE_SETTINGS_ADD_IMAGES_BUTTON_TAPPED,
    PRODUCT_IMAGE_SETTINGS_ADD_IMAGES_SOURCE_TAPPED,
    PRODUCT_IMAGE_SETTINGS_DELETE_IMAGE_BUTTON_TAPPED,
    PRODUCT_SETTINGS_STATUS_TAPPED,
    PRODUCT_SETTINGS_CATALOG_VISIBILITY_TAPPED,
    PRODUCT_SETTINGS_SLUG_TAPPED,
    PRODUCT_SETTINGS_PURCHASE_NOTE_TAPPED,
    PRODUCT_SETTINGS_VISIBILITY_TAPPED,
    PRODUCT_SETTINGS_MENU_ORDER_TAPPED,
    PRODUCT_SETTINGS_REVIEWS_TOGGLED,

    // -- Product filters
    PRODUCT_FILTER_LIST_SHOW_PRODUCTS_BUTTON_TAPPED,
    PRODUCT_FILTER_LIST_CLEAR_MENU_BUTTON_TAPPED,

    // -- Product variations
    PRODUCT_VARIANTS_PULLED_TO_REFRESH,
    PRODUCT_VARIANTS_LOADED,
    PRODUCT_VARIANTS_LOAD_ERROR,
    PRODUCT_VARIANTS_BULK_UPDATE_TAPPED,
    PRODUCT_VARIANTS_BULK_UPDATE_REGULAR_PRICE_TAPPED,
    PRODUCT_VARIANTS_BULK_UPDATE_SALE_PRICE_TAPPED,
    PRODUCT_VARIANTS_BULK_UPDATE_REGULAR_PRICE_DONE_TAPPED,
    PRODUCT_VARIANTS_BULK_UPDATE_SALE_PRICE_DONE_TAPPED,
    PRODUCT_VARIANTS_BULK_UPDATE_STOCK_QUANTITY_TAPPED,
    PRODUCT_VARIANTS_BULK_UPDATE_STOCK_QUANTITY_DONE_TAPPED,

    // -- Product images
    PRODUCT_IMAGE_ADDED,

    // -- Duplicate product
    DUPLICATE_PRODUCT_SUCCESS,
    DUPLICATE_PRODUCT_FAILED,
    PRODUCT_DETAIL_DUPLICATE_BUTTON_TAPPED,

    // -- Help & Support
    SUPPORT_HELP_CENTER_VIEWED(siteless = true),
    SUPPORT_IDENTITY_SET(siteless = true),
    SUPPORT_IDENTITY_FORM_VIEWED(siteless = true),
    SUPPORT_APPLICATION_LOG_VIEWED(siteless = true),
    SUPPORT_SSR_COPY_BUTTON_TAPPED,

    // -- Support Request Form
    SUPPORT_NEW_REQUEST_VIEWED,
    SUPPORT_NEW_REQUEST_CREATED,
    SUPPORT_NEW_REQUEST_FAILED,

    // -- Push notifications
    PUSH_NOTIFICATION_RECEIVED,
    PUSH_NOTIFICATION_TAPPED,

    // -- Notifications List
    NOTIFICATION_OPEN,
    NOTIFICATIONS_LOADED,
    NOTIFICATIONS_LOAD_FAILED,

    // -- Product Review List
    REVIEWS_LOADED,
    REVIEWS_LOAD_FAILED,
    REVIEWS_PRODUCTS_LOADED,
    REVIEWS_PRODUCTS_LOAD_FAILED,
    REVIEWS_MARK_ALL_READ,
    REVIEWS_MARK_ALL_READ_SUCCESS,
    REVIEWS_MARK_ALL_READ_FAILED,
    REVIEWS_LIST_PULLED_TO_REFRESH,
    REVIEWS_LIST_MENU_MARK_READ_BUTTON_TAPPED,

    // -- Product Review Detail
    REVIEW_OPEN,
    REVIEW_LOADED,
    REVIEW_LOAD_FAILED,
    REVIEW_PRODUCT_LOADED,
    REVIEW_PRODUCT_LOAD_FAILED,
    REVIEW_MARK_READ,
    REVIEW_MARK_READ_SUCCESS,
    REVIEW_MARK_READ_FAILED,
    REVIEW_ACTION,
    REVIEW_ACTION_FAILED,
    REVIEW_ACTION_SUCCESS,
    REVIEW_DETAIL_APPROVE_BUTTON_TAPPED,
    REVIEW_DETAIL_OPEN_EXTERNAL_BUTTON_TAPPED,
    REVIEW_DETAIL_SPAM_BUTTON_TAPPED,
    REVIEW_DETAIL_TRASH_BUTTON_TAPPED,

    // -- In-App Feedback
    APP_FEEDBACK_PROMPT,
    APP_FEEDBACK_RATE_APP,
    SURVEY_SCREEN,
    FEATURE_FEEDBACK_BANNER,

    // -- Errors
    JETPACK_TUNNEL_TIMEOUT,

    // -- Order status changes
    SET_ORDER_STATUS_DIALOG_APPLY_BUTTON_TAPPED,

    // -- Application permissions
    APP_PERMISSION_GRANTED,
    APP_PERMISSION_DENIED,
    APP_PERMISSION_RATIONALE_ACCEPTED,
    APP_PERMISSION_RATIONALE_DISMISSED,

    // -- Encrypted logging
    ENCRYPTED_LOGGING_UPLOAD_SUCCESSFUL,
    ENCRYPTED_LOGGING_UPLOAD_FAILED,

    // -- What's new / feature announcements
    FEATURE_ANNOUNCEMENT_SHOWN,

    // -- Jetpack CP
    JETPACK_CP_SITES_FETCHED,
    FEATURE_JETPACK_BENEFITS_BANNER,
    JETPACK_INSTALL_BUTTON_TAPPED,
    JETPACK_INSTALL_SUCCEEDED,
    JETPACK_INSTALL_FAILED,
    JETPACK_INSTALL_IN_WPADMIN_BUTTON_TAPPED,
    JETPACK_INSTALL_CONTACT_SUPPORT_BUTTON_TAPPED,
    JETPACK_BENEFITS_LOGIN_BUTTON_TAPPED,
    JETPACK_SETUP_CONNECTION_CHECK_COMPLETED,
    JETPACK_SETUP_CONNECTION_CHECK_FAILED,
    JETPACK_SETUP_LOGIN_FLOW,
    JETPACK_SETUP_LOGIN_COMPLETED,
    JETPACK_SETUP_FLOW,
    JETPACK_SETUP_COMPLETED,
    JETPACK_SETUP_SYNCHRONIZATION_COMPLETED,

    // -- Other
    UNFULFILLED_ORDERS_LOADED,
    TOP_EARNER_PRODUCT_TAPPED,

    // -- Media picker
    MEDIA_PICKER_PREVIEW_OPENED,
    MEDIA_PICKER_RECENT_MEDIA_SELECTED,
    MEDIA_PICKER_OPEN_GIF_LIBRARY,
    MEDIA_PICKER_OPEN_DEVICE_LIBRARY,
    MEDIA_PICKER_CAPTURE_PHOTO,
    MEDIA_PICKER_SEARCH_TRIGGERED,
    MEDIA_PICKER_SEARCH_EXPANDED,
    MEDIA_PICKER_SEARCH_COLLAPSED,
    MEDIA_PICKER_SHOW_PERMISSIONS_SCREEN,
    MEDIA_PICKER_ITEM_SELECTED,
    MEDIA_PICKER_ITEM_UNSELECTED,
    MEDIA_PICKER_SELECTION_CLEARED,
    MEDIA_PICKER_OPENED,
    MEDIA_PICKER_OPEN_SYSTEM_PICKER,
    MEDIA_PICKER_OPEN_WORDPRESS_MEDIA_LIBRARY_PICKER,

    // -- More Menu (aka Hub Menu)
    HUB_MENU_SWITCH_STORE_TAPPED,
    HUB_MENU_OPTION_TAPPED,
    HUB_MENU_SETTINGS_TAPPED,

    // Shortcuts
    SHORTCUT_PAYMENTS_TAPPED,
    SHORTCUT_ORDERS_ADD_NEW,

    // Inbox
    INBOX_NOTES_LOADED,
    INBOX_NOTES_LOAD_FAILED,
    INBOX_NOTE_ACTION,

    // Coupons
    COUPONS_LOADED,
    COUPONS_LOAD_FAILED,
    COUPONS_LIST_SEARCH_TAPPED,
    COUPON_DETAILS,
    COUPON_UPDATE_INITIATED,
    COUPON_UPDATE_SUCCESS,
    COUPON_UPDATE_FAILED,
    COUPON_DELETE_SUCCESS,
    COUPON_DELETE_FAILED,
    COUPON_CREATION_SUCCESS,
    COUPON_CREATION_FAILED,
    COUPON_CREATION_INITIATED,

    // Onboarding
    LOGIN_ONBOARDING_SHOWN,
    LOGIN_ONBOARDING_NEXT_BUTTON_TAPPED,
    LOGIN_ONBOARDING_SKIP_BUTTON_TAPPED,

    // Woo Installation
    LOGIN_WOOCOMMERCE_SETUP_BUTTON_TAPPED,
    LOGIN_WOOCOMMERCE_SETUP_DISMISSED,
    LOGIN_WOOCOMMERCE_SETUP_COMPLETED,

    // Login help scheduled notifications
    LOCAL_NOTIFICATION_SCHEDULED,
    LOCAL_NOTIFICATION_DISPLAYED,
    LOCAL_NOTIFICATION_TAPPED,
    LOCAL_NOTIFICATION_DISMISSED,
    FREE_TRIAL_SURVEY_SENT,
    FREE_TRIAL_SURVEY_DISPLAYED,

    // Widgets
    WIDGET_TAPPED,

    // App links
    UNIVERSAL_LINK_OPENED,
    UNIVERSAL_LINK_FAILED,

    // Analytics Hub
    ANALYTICS_HUB_WAITING_TIME_LOADED,

    // Site creation native flow
    SITE_CREATION_FAILED(siteless = true),
    SITE_CREATION_DISMISSED(siteless = true),
    SITE_CREATION_SITE_LOADING_RETRIED(siteless = true),
    SITE_CREATION_SITE_PREVIEWED,
    SITE_CREATION_STORE_MANAGEMENT_OPENED,
    SITE_CREATION_STEP(siteless = true),
    SITE_CREATION_IAP_ELIGIBILITY(siteless = true),
    SITE_CREATION_IAP_ELIGIBILITY_ERROR(siteless = true),
    SITE_CREATION_IAP_PURCHASE_SUCCESS(siteless = true),
    SITE_CREATION_IAP_PURCHASE_ERROR(siteless = true),
    SITE_CREATION_PROFILER_DATA(siteless = true),
    SITE_CREATION_PROFILER_QUESTION_SKIPPED(siteless = true),
    SITE_CREATION_TRY_FOR_FREE_TAPPED(siteless = true),
    SITE_CREATION_TIMED_OUT(siteless = true),
    SITE_CREATION_PROPERTIES_OUT_OF_SYNC(siteless = true),
    SITE_CREATION_FREE_TRIAL_CREATED_SUCCESS,
    SITE_CREATION_FLOW_STARTED,

    // Domain change
    CUSTOM_DOMAINS_STEP,
    DOMAIN_CONTACT_INFO_VALIDATION_FAILED,
    CUSTOM_DOMAIN_PURCHASE_SUCCESS,
    CUSTOM_DOMAIN_PURCHASE_FAILED,

    // Application passwords login
    APPLICATION_PASSWORDS_NEW_PASSWORD_CREATED,
    APPLICATION_PASSWORDS_GENERATION_FAILED,
    APPLICATION_PASSWORDS_AUTHORIZATION_WEB_VIEW_SHOWN,
    APPLICATION_PASSWORDS_AUTHORIZATION_REJECTED,
    APPLICATION_PASSWORDS_AUTHORIZATION_APPROVED,
    APPLICATION_PASSWORDS_AUTHORIZATION_URL_NOT_AVAILABLE,

    // Free Trial
    FREE_TRIAL_UPGRADE_NOW_TAPPED,
    PLAN_UPGRADE_SUCCESS,
    PLAN_UPGRADE_ABANDONED,
    UPGRADES_REPORT_SUBSCRIPTION_ISSUE_TAPPED,

    // Store onboarding
    STORE_ONBOARDING_SHOWN,
    STORE_ONBOARDING_TASK_TAPPED,
    STORE_ONBOARDING_TASK_COMPLETED,
    STORE_ONBOARDING_COMPLETED,
    STORE_ONBOARDING_HIDE_LIST,
    STORE_ONBOARDING_WCPAY_BEGIN_SETUP_TAPPED,
    STORE_ONBOARDING_WCPAY_TERMS_CONTINUE_TAPPED,

    // Quantity rules (Min/Max extension)
    PRODUCT_DETAIL_VIEW_QUANTITY_RULES_TAPPED,
    PRODUCT_VARIATION_VIEW_QUANTITY_RULES_TAPPED,

    // Bundled products
    PRODUCT_DETAIL_VIEW_BUNDLED_PRODUCTS_TAPPED,

    // Composite Products
    PRODUCT_DETAILS_VIEW_COMPONENTS_TAPPED,

    // Account
    CLOSE_ACCOUNT_TAPPED,
    CLOSE_ACCOUNT_SUCCESS,
    CLOSE_ACCOUNT_FAILED,

    // EU Shipping Notice
    EU_SHIPPING_NOTICE_SHOWN,
    EU_SHIPPING_NOTICE_DISMISSED,
    EU_SHIPPING_NOTICE_LEARN_MORE_TAPPED,

    // Privacy Banner
    PRIVACY_CHOICES_BANNER_PRESENTED,
    PRIVACY_CHOICES_BANNER_SETTINGS_BUTTON_TAPPED,
    PRIVACY_CHOICES_BANNER_SAVE_BUTTON_TAPPED,

    // AI Features
    PRODUCT_SHARING_AI_DISPLAYED,
    PRODUCT_SHARING_AI_GENERATE_TAPPED,
    PRODUCT_SHARING_AI_SHARE_TAPPED,
    PRODUCT_SHARING_AI_DISMISSED,
    PRODUCT_SHARING_AI_MESSAGE_GENERATED,
    PRODUCT_SHARING_AI_MESSAGE_GENERATION_FAILED,

    PRODUCT_DESCRIPTION_AI_BUTTON_TAPPED,
    PRODUCT_DESCRIPTION_AI_GENERATE_BUTTON_TAPPED,
    PRODUCT_DESCRIPTION_AI_APPLY_BUTTON_TAPPED,
    PRODUCT_DESCRIPTION_AI_COPY_BUTTON_TAPPED,
    PRODUCT_DESCRIPTION_AI_GENERATION_SUCCESS,
    PRODUCT_DESCRIPTION_AI_GENERATION_FAILED,
    PRODUCT_AI_FEEDBACK,

    PRODUCT_NAME_AI_ENTRY_POINT_TAPPED,
    PRODUCT_NAME_AI_GENERATE_BUTTON_TAPPED,
    PRODUCT_NAME_AI_COPY_BUTTON_TAPPED,
    PRODUCT_NAME_AI_APPLY_BUTTON_TAPPED,
    PRODUCT_NAME_AI_PACKAGE_IMAGE_BUTTON_TAPPED,
    PRODUCT_NAME_AI_GENERATION_SUCCESS,
    PRODUCT_NAME_AI_GENERATION_FAILED,

    PRODUCT_CREATION_AI_ENTRY_POINT_DISPLAYED,
    PRODUCT_CREATION_AI_ENTRY_POINT_TAPPED,
    PRODUCT_CREATION_AI_PRODUCT_NAME_CONTINUE_BUTTON_TAPPED,
    PRODUCT_CREATION_AI_TONE_SELECTED,
    PRODUCT_CREATION_AI_GENERATE_DETAILS_TAPPED,
    PRODUCT_CREATION_AI_GENERATE_PRODUCT_DETAILS_SUCCESS,
    PRODUCT_CREATION_AI_GENERATE_PRODUCT_DETAILS_FAILED,
    PRODUCT_CREATION_AI_SAVE_AS_DRAFT_BUTTON_TAPPED,
    PRODUCT_CREATION_AI_SAVE_AS_DRAFT_SUCCESS,
    PRODUCT_CREATION_AI_SAVE_AS_DRAFT_FAILED,

    ADD_PRODUCT_FROM_IMAGE_DISPLAYED,
    ADD_PRODUCT_FROM_IMAGE_SCAN_COMPLETED,
    ADD_PRODUCT_FROM_IMAGE_SCAN_FAILED,
    ADD_PRODUCT_FROM_IMAGE_DETAILS_GENERATED,
    ADD_PRODUCT_FROM_IMAGE_DETAIL_GENERATION_FAILED,
    ADD_PRODUCT_FROM_IMAGE_CONTINUE_BUTTON_TAPPED,
    ADD_PRODUCT_FROM_IMAGE_CHANGE_PHOTO_BUTTON_TAPPED,
    ADD_PRODUCT_FROM_IMAGE_REGENERATE_BUTTON_TAPPED,

    AI_IDENTIFY_LANGUAGE_SUCCESS,
    AI_IDENTIFY_LANGUAGE_FAILED,

    // Blaze
    BLAZE_ENTRY_POINT_DISPLAYED,
    BLAZE_ENTRY_POINT_TAPPED,
    BLAZE_BANNER_DISMISSED,
    BLAZE_FLOW_STARTED,
    BLAZE_FLOW_CANCELED,
    BLAZE_FLOW_COMPLETED,
    BLAZE_FLOW_ERROR,
    BLAZE_CAMPAIGN_DETAIL_SELECTED,
    BLAZE_CAMPAIGN_LIST_ENTRY_POINT_SELECTED,
    BLAZE_INTRO_DISPLAYED,

    // Hazmat Shipping Declaration
    CONTAINS_HAZMAT_CHECKED,
    HAZMAT_CATEGORY_SELECTOR_OPENED,
    HAZMAT_CATEGORY_SELECTED,

    // -- Bundles
    ORDER_FORM_BUNDLE_PRODUCT_CONFIGURE_CTA_SHOWN,
    ORDER_FORM_BUNDLE_PRODUCT_CONFIGURE_CTA_TAPPED,
    ORDER_FORM_BUNDLE_PRODUCT_CONFIGURATION_CHANGED,
    ORDER_FORM_BUNDLE_PRODUCT_CONFIGURATION_SAVE_TAPPED
}
