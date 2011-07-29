package com.cphse.queue;

public enum OrderType {
	MAIL_ACCOUNT_FETCH, // Fetch from a mail account 
	FETCH_MAIL_ACCOUNTS, // Trigger order creation for all valid mail accounts not already triggered.
	TEST
}
