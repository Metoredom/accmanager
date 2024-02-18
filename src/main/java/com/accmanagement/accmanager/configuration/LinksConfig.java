package com.accmanagement.accmanager.configuration;

public class LinksConfig {

    public final static String CLIENTS_URI = "/clients";
    public final static String ACCOUNTS_URI = "/accounts";

    public final static String CLIENT_ACCOUNTS_URI = CLIENTS_URI + "/{id}" + "/accounts";
    public final static String ACCOUNT_HISTORY_URI = ACCOUNTS_URI + "/{id}" + "/history";
    public final static String TRANSFER_FUNDS_URI = "/transfer";

    public final static String CONVERT_API_URI = "https://api.apilayer.com/exchangerates_data/convert?to=%s&from=%s&amount=%f";

}
