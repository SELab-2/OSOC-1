import { uniqueAtom, uniqueSelector } from '.';
import { emailRegex, nameRegex, passwordRegex1 } from '../lib/regex';

export const nameState = uniqueAtom({
  name: 'registerNameState',
  defaultValue: '',
});

export const validNameState = uniqueSelector({
  name: 'validNameState',
  getter: ({ get }) => {
    const name = get(nameState);

    // we use a name Regex to check if the name is valid
    return nameRegex.test(name);
  },
});

export const emailState = uniqueAtom({
  name: 'registerEmailState',
  defaultValue: '',
});

export const validEmailState = uniqueSelector({
  name: 'validEmailState',
  getter: ({ get }) => {
    const email = get(emailState);

    // we use an email Regex to check if the email is valid
    return emailRegex.test(email);
  },
});

export const passwordState = uniqueAtom({
  name: 'registerPasswordState',
  defaultValue: '',
});

export const validPasswordState = uniqueSelector({
  name: 'validPasswordState',
  getter: ({ get }) => {
    const password = get(passwordState);

    // we use a password Regex to check if we are dealing with a valid password
    return passwordRegex1.test(password);
  },
});

export const repeatPasswordState = uniqueAtom({
  name: 'registerRepeatPasswordState',
  defaultValue: '',
});

export const validRepeatPasswordState = uniqueSelector({
  name: 'validRepeatPasswordState',
  getter: ({ get }) => {
    const password = get(passwordState);
    const repeatPassword = get(repeatPasswordState);

    // check if it's a valid password and the same as the first password
    return passwordRegex1.test(repeatPassword) && password === repeatPassword;
  },
});
