/**
 * This file was auto-generated by swagger-to-ts.
 * Do not make direct changes to the file.
 */

export interface components {
  schemas: {
    Result: { statusCode?: number; information?: string };
    SigninDataRequestModel: { yesQuestion?: string; temperature?: number };
    SigninRequestModel: {
      signinData?: components["schemas"]["SigninDataRequestModel"];
      user?: components["schemas"]["UserRequestModel"];
    };
    UserRequestModel: { name?: string; email?: string };
    NewUserRequestModel: {
      user?: components["schemas"]["UserRequestModel"];
      signature?: string;
      signatureData?: string;
      signinData?: components["schemas"]["SigninDataRequestModel"];
      signatureDate?: string;
    };
    UserExistsResult: {
      statusCode?: number;
      information?: string;
      userExists?: boolean;
    };
  };
}
