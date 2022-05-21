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
    if (!user || !user.role) {
      router.push('/login');
    } else if (edition) {
      router.push(`/${edition}/projects`);
    } else {
      switch (user.role) {
        case UserRole.Admin:
          router.push('/editions');
          break;

        case UserRole.Coach:
          router.push('/users');
          break;

        case UserRole.Disabled:
          router.push('/wait');
          break;
      }
    }
  }, [user, edition, router]);

  return <></>;
};
export default Index;
