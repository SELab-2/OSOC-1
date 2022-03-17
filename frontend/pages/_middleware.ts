import { NextApiRequest } from 'next';
import { getToken } from 'next-auth/jwt';
import { NextRequest, NextResponse } from 'next/server';

/*
 * A 'hacky' way to get around the next typing issues for middelware
 */
type NextMiddlewareRequest = NextApiRequest & NextRequest;

/**
 * Middleware for every Next request
 * 
 * @param req - the incoming Next request
 * @returns a NextResponse object containing the next step
 */
export async function middleware(req: NextMiddlewareRequest) {
  // Token will exist if user is logged in
  const token = await getToken({ req, secret: process.env.JWT_SECRET });

  const { pathname } = req.nextUrl;

  /**
   *  We allow the request to be made if:
   *  - It contains a valid token (which means the user is logged in)
   *  - It is trying to make a request to the authentication point
   */

  if (pathname.includes('/api/auth')) {
    return NextResponse.next();
  }

  if (token) {
    // Check if the user account is disabled

    // if disabled, redirect to wait page
    const url = req.nextUrl.clone();
    url.pathname = '/wait';
    return NextResponse.redirect(url);
  }

  // clone the url to use in the redirect because NextJS 12.1 does not allow relative URLs anymore
  const url = req.nextUrl.clone();
  url.pathname = '/login';

  // if the request is trying to access protected resources, we redirect them to the login
  // TODO: remove /wait page filter when accounts are working
  if (!token && !(pathname === '/login' || pathname === '/register' || pathname === '/wait')) {
    return NextResponse.redirect(url);
  }
}
