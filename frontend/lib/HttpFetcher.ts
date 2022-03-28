import Endpoints from './endpoints';

export type PostJSONProps = {
  /**
   * The endpoint to make the request to
   * @see {@link Endpoints} for available options
   */
  endpoint: Endpoints;

  /**
   * An optional access token to make authenticated requests
   *
   * @remarks
   * More information can be found in the authentication docs
   */
  accessToken?: string;

  /**
   * The body for the request
   */
  body: Record<string, unknown>;
};

export type PostURLEProps = {
  /**
   * The endpoint to make the request to
   * @see {@link ENDPOINT_ENUM | The Endpoint Enum} for available options
   */
  endpoint: Endpoints;

  /**
   * An optional access token to make authenticated requests
   *
   * @remarks
   * More information can be found in the authentication docs
   */
  accessToken?: string;

  /**
   * The body for the request
   */
  body: Record<string, string>;
};

class NamedError extends Error {
  get name() {
    return this.constructor.name;
  }
}

/**
 * Custom Error type for web api errors
 * {@label WEB_API_ERROR}
 */
export class WebApiError extends NamedError {
  statusCode: number;

  constructor(body: Record<string, unknown>, statusCode: number) {
    const message =
      body && body.error
        ? `${statusCode} Error: ${body.error}`
        : `Error while trying to connect to web api. Statuscode: ${statusCode}`;
    super(message);
    this.statusCode = statusCode;
  }
}

/**
 * A helper class to make requests to outside sources
 *
 * @remarks
 * This class only exists out of static functions, so there's no need
 * to instantiate this class
 */
class HttpFetcher {
  /**
   * Helper function to make post requests
   *
   * @param endpoint - the endpoint to make a request to
   * @param body - the body to use in the POST request
   * @param contentType - the content type header
   * @param accessToken - the access token for the Basic authorization header
   * @returns the response from a POST request
   *
   * @throws {@link WEB_API_ERROR}
   * This error is thrown when the request fails
   */
  static async _postRequest({
    endpoint,
    body,
    contentType,
    ...props
  }: {
    endpoint: Endpoints;
    body: BodyInit;
    contentType: string;
    accessToken?: string;
  }): Promise<Response> {
    const options: RequestInit = {
      method: 'POST',
      headers: {
        'Content-Type': contentType,
      },
      body,
    };

    if (props.accessToken) {
      options.headers = {
        Authorization: `Basic ${props.accessToken}`,
        ...options.headers,
      };
    }

    const request = fetch(endpoint, options);

    const response = await request;

    if (!response.ok)
      throw new WebApiError(
        (await response.json()) || response.statusText,
        response.status
      );

    return response;
  }

  /**
   * Helper function to make JSON POST requests
   *
   * @param endpoint - the endpoint to make a request to
   * @param body - the body to use in the POST request
   * @param contentType - the content type header
   * @param accessToken - the access token for the Basic authorization header
   * @returns the response from a POST request
   *
   * @throws {@link WEB_API_ERROR}
   * This error is thrown when the request fails
   */
  static async postJSON({
    endpoint,
    body,
    ...props
  }: PostJSONProps): Promise<Response> {
    const response = await this._postRequest({
      endpoint,
      body: JSON.stringify(body),
      contentType: 'application/json',
      ...props,
    });

    return response;
  }

  /**
   * Helper function to make URL Encoded POST requests
   *
   * @param endpoint - the endpoint to make a request to
   * @param body - the body to use in the POST request
   * @param contentType - the content type header
   * @param accessToken - the access token for the Basic authorization header
   * @returns the response from a POST request
   *
   * @throws {@link WebApiError}
   * This error is thrown when the request fails
   */
  static async postURLEncoded({
    endpoint,
    body,
    ...props
  }: PostURLEProps): Promise<Response> {
    const response = await this._postRequest({
      endpoint,
      body: new URLSearchParams(body),
      contentType: 'application/x-www-form-urlencoded',
      ...props,
    });

    return response;
  }
}

export default HttpFetcher;
