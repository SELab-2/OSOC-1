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
  body: Record<string, unknown> | unknown;
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

export type getProps = {
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
}

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

  static async _makeBodyRequest({
    endpoint,
    body,
    contentType,
    method,
    ...props
  }: {
    endpoint: Endpoints;
    body: BodyInit,
    contentType: string;
    method: string;
    accessToken?: string;
  }): Promise<Response> {
    const options: RequestInit = {
      method,
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
    accessToken
  }: {
    endpoint: Endpoints;
    body: BodyInit;
    contentType: string;
    accessToken?: string;
  }): Promise<Response> {
    return await this._makeBodyRequest({
      endpoint,
      body,
      contentType,
      method: 'POST',
      accessToken
    });
  }

  static async _patchRequest({
    endpoint,
    body,
    contentType,
    accessToken
  }: {
    endpoint: Endpoints;
    body: BodyInit;
    contentType: string;
    accessToken?: string;
  }): Promise<Response> {
    return await this._makeBodyRequest({
      endpoint,
      body,
      contentType,
      method: 'PATCH',
      accessToken
    });
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

  /**
   * Helper function to make GET requests
   *
   * @param endpoint - the endpoint to make a request to
   * @param accessToken - the access token for the Basic authorization header
   * @returns the response from a GET request
   *
   * @throws {@link WebApiError}
   * This error is thrown when the request fails
   */  
  static async get({
    endpoint,
    ...props
  }: getProps): Promise<Response> {
    const options: RequestInit = {
      method: 'GET'
    };

    if (props.accessToken) {
      options.headers = {
        Authorization: `Basic ${props.accessToken}`,
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

  static async patchJSON({
    endpoint,
    body,
    accessToken
  }: PostJSONProps): Promise<Response> {
    return await this._patchRequest({
      endpoint,
      body: JSON.stringify(body),
      contentType: 'application/json',
      accessToken
    });
  }

  static async delete({
    endpoint,
    accessToken
  }: {
    endpoint: Endpoints;
    accessToken?: string;
  }): Promise<Response> {
    const options: RequestInit = {
      method: 'DELETE'
    };

    if (accessToken) {
      options.headers = {
        Authorization: `Basic ${accessToken}`,
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
}

export default HttpFetcher;
