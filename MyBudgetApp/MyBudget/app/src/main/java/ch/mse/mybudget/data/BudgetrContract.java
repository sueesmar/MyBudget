package ch.mse.mybudget.data;

import android.os.StrictMode;
import android.provider.BaseColumns;

/**
 * Created by Marcel on 05.11.2016.
 */

public class BudgetrContract {

    private BudgetrContract(){}

    /* Inner class that defines the table contents */
    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_REGDATE = "regdate";
    }

    /* Inner class that defines the table contents */
    public static class ExpenditureEntry implements BaseColumns {
        public static final String TABLE_NAME = "expenditure";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_DESCRIPTION= "description";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_RECEIPT = "receipt";
        public static final String COLUMN_NAME_PLACE = "place";
        public static final String COLUMN_NAME_PAYMENTMETHODE = "paymentmethode";
        public static final String COLUMN_NAME_USER_ID = "userid";
    }

    /* Inner class that defines the table contents */
    public static class SalaryEntry implements BaseColumns {
        public static final String TABLE_NAME = "salary";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_SALARYMOUNT = "salarymount";
        public static final String COLUMN_NAME_SALARYDATE = "salarydate";
        public static final String COLUMN_NAME_USER_ID = "userid";
    }


}
