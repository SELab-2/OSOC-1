import { useRouter } from 'next/router';
import { useEffect } from 'react';
import useEdition from '../hooks/useEdition';
import useUser from '../hooks/useUser';
import { UserRole } from '../lib/types';

const Index = () => {
  const router = useRouter();
  const [edition] = useEdition();
  const [user] = useUser();

  useEffect(() => {
    if (edition) {
      router.push(`/${edition}/projects`);
    } else {
      switch (user.role) {
        case UserRole.Admin:
          router.push('/editions');
          break;

        case UserRole.Coach:
          router.push('/users');
          break;

        default:
          router.push('/wait');
          break;
      }
    }
  }, []);

  return <></>;
};
export default Index;
