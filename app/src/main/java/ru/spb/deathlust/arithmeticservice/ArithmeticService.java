package ru.spb.deathlust.arithmeticservice;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;

public class ArithmeticService extends IntentService {
    public static final String OPERATION = "operation";
    public static final String X = "x";
    public static final String Y = "y";

    public static final String PENDING_INTENT = "pending_intent";
    public static final int STATUS_FINISH = 0;
    public static final String RESULT = "result";
    public static final int STATUS_ERROR = 1;
    public static final String ERROR = "error";

    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String MULTIPLY = "*";
    public static final String DIVIDE = "/";

    public ArithmeticService() {
        super("calculator");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!intent.hasExtra(PENDING_INTENT)) {
            return;
        }
        PendingIntent pi = intent.getParcelableExtra(PENDING_INTENT);
        if (pi == null) {
            return;
        }
        String errorMessage = null;
        double result = 0;
        if (intent.hasExtra(X) && intent.hasExtra(Y) && intent.hasExtra(OPERATION)) {
            String op = intent.getStringExtra(OPERATION);
            double x = intent.getDoubleExtra(X, 0);
            double y = intent.getDoubleExtra(Y, 0);
            switch (op) {
                case PLUS:
                    result = x + y;
                    break;
                case MINUS:
                    result = x - y;
                    break;
                case MULTIPLY:
                    result = x * y;
                    break;
                case DIVIDE:
                    if (y == 0) {
                        errorMessage = getString(R.string.zero_division);
                        break;
                    }
                    result = x / y;
                    break;
                default:
                    errorMessage = getString(R.string.wrong_operation);
            }
        } else {
            errorMessage = getString(R.string.parameter_count_mismatch);
        }
        try {
            if (errorMessage != null) {
                pi.send(this, STATUS_ERROR, new Intent().putExtra(ERROR, errorMessage));
            } else {
                pi.send(this, STATUS_FINISH, new Intent().putExtra(RESULT, result));
            }
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
