package ca.etsmtl.applets.etsmobile.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.AuthentificationPortailTask;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
import ca.etsmtl.applets.etsmobile.model.UserCredentials;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends AccountAuthenticatorActivity implements RequestListener<Object> {

    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;

    // UI references.
    @Bind(R.id.email)
    EditText mEmailView;
    @Bind(R.id.password)
    EditText mPasswordView;
    @Bind(R.id.login_form)
    View mLoginFormView;
    @Bind(R.id.login_status)
    View mLoginStatusView;
    @Bind(R.id.login_status_message)
    TextView mLoginStatusMessageView;

    private DataManager dataManager;
    private UserCredentials userCredentials;

    private AccountManager accountManager;
    public AccountAuthenticatorResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        dataManager = DataManager.getInstance(getApplicationContext());


        accountManager = AccountManager.get(getBaseContext());

        // Set up the login form.
        mEmailView.setText(mEmail);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!mEmail.matches("[a-zA-z]{2}(\\d){5}")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            userCredentials = new UserCredentials(mEmail, mPassword);

            dataManager.login(userCredentials, this);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which
                // allow for very easy animations. If available, use these APIs to
                // fade-in the progress spinner.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                    int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                    mLoginStatusView.setVisibility(View.VISIBLE);
                    mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                                }
                            });

                    mLoginFormView.setVisibility(View.VISIBLE);
                    mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                                }
                            });
                } else {
                    // The ViewPropertyAnimator APIs are not available, so simply show
                    // and hide the relevant UI components.
                    mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }

            }
        });
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        showProgress(false);
        mPasswordView.setError(getString(R.string.error_invalid_email));
        mPasswordView.requestFocus();
    }

    @Override
    public void onRequestSuccess(Object o) {
        showProgress(false);

        if (o != null) {
            Etudiant etudiant = (Etudiant) o;
            if (etudiant.erreur != null) {
                mPasswordView.setError(getString(R.string.error_invalid_pwd));
                mPasswordView.requestFocus();
            } else {
                ApplicationManager.userCredentials = userCredentials;


                String accountName = userCredentials.getUsername();
                String accountPassword = userCredentials.getPassword();

                createETSMobileAccount(accountName, accountPassword);

                startActivity(new Intent(LoginActivity.this, MainActivity.class));

                //Run authentication to monETS in another thread not to slow app
                new AuthentificationPortailTask(this).execute(
                        getString(R.string.portail_api_authentification_url),
                        ApplicationManager.userCredentials.getUsername(),
                        ApplicationManager.userCredentials.getPassword());

                finishActivity(1);
            }

        } else {
            mPasswordView.setError(getString(R.string.error_invalid_email));
            mPasswordView.requestFocus();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataManager.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataManager.stop();
    }

    private void createETSMobileAccount(String accountName, String accountPassword) {
        final Account account = new Account(accountName, Constants.ACCOUNT_TYPE);

        if (getIntent().getBooleanExtra(Constants.KEY_IS_ADDING_NEW_ACCOUNT, false)) {

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            accountManager.addAccountExplicitly(account, accountPassword, null);

        } else {
            accountManager.setPassword(account, accountPassword);
        }

        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
    }


}
