import axios from "axios";

const envURL: string = process.env.NEXT_PUBLIC_API_ENDPOINT || 'http://localhost:8080';
const BASEURL = envURL.replace(/\/$/, '');

export default axios.create({
  baseURL: BASEURL
});