import { NextApiRequest } from "next";
import { getToken } from "next-auth/jwt";
import { NextRequest, NextResponse } from "next/server";

// A 'hacky' way to get around the next typing issues for middelware
type NextMiddlewareRequest = NextApiRequest & NextRequest;

export async function middleware(req: NextMiddlewareRequest) {

  // Token will exist if user is logged in
  const token = await getToken({ req, secret: process.env.JWT_SECRET });

  const { pathname } = req.nextUrl;

  /**
   *  We allow the request to be made if:
   *  - It contains a valid token (which means the user is logged in)
   *  - It is trying to make a request to the authentication point
   */

  if (pathname.includes('/api/auth') || token) {
    return NextResponse.next();
  }

  // clone the url to use in the redirect because NextJS 12.1 does not allow relative URLs anymore
  const url = req.nextUrl.clone()
  url.pathname = '/login'

  // if the request is trying to access protected resources, we redirect them to the login
  if (!token && pathname !== '/login') {
    return NextResponse.redirect(url);
  }

};