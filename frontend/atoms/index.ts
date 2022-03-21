/**
 *  IMPORTANT NOTE
 *
 * A certain feature of NextJS (at the time of writing, it is unclear which feature this is) causes
 * Recoil to throw errors because of duplicate atom keys. This of course isn't the case and it doesn't break when used.
 * To get around this issue, we are generating a random uuid every time this file is re-run, that way we won't get the
 * duplicate key warning. This seems like an unsafe way to fix this, but I can assure you, it works as intended.
 */
import { atom, GetCallback, GetRecoilValue, selector } from 'recoil';
import { v4 } from 'uuid';

type UniqueAtomParams<T> = {
  name: string;
  defaultValue: T;
};

export const uniqueAtom = <T>({ name, defaultValue }: UniqueAtomParams<T>) =>
  atom({
    key: `${name}/${v4()}`,
    default: defaultValue,
  });

type UniqueSelectorParams = {
  name: string;
  getter: (opts: { get: GetRecoilValue; getCallback: GetCallback }) => unknown;
};

export const uniqueSelector = ({ name, getter }: UniqueSelectorParams) =>
  selector({
    key: `${name}/${v4()}`,
    get: getter,
  });
