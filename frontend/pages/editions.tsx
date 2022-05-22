import { NextPage } from 'next';
import EditionCard from '../components/editions/EditionCard';
import Header from '../components/Header';
import { PlusCircleIcon } from '@heroicons/react/outline';
import { useRouter } from 'next/router';
import useEdition from '../hooks/useEdition';
import useAxiosAuth from '../hooks/useAxiosAuth';
import Endpoints from '../lib/endpoints';
import { useEffect, useState } from 'react';
import EditionCreateForm from '../components/editions/EditionCreateForm';
import Error from '../components/Error';
import { Edition, UserRole } from '../lib/types';
import RouteProtection from '../components/RouteProtection';
import EditionDeletionPopup from '../components/editions/EditionDeletionPopup';
import PersistLogin from '../components/PersistLogin';
import Head from 'next/head';
import { parseError } from '../lib/requestUtils';

/**
 * Editions page where we list editions, show a form to create new editions and
 * make it possible to view data from previous editions
 * {@label EDITIONS_PAGE}
 *
 * @returns Editions Page
 */
const Editions: NextPage = () => {
  const router = useRouter();
  const [, setEdition] = useEdition();
  const [allEditions, setAllEditions] = useState([] as Edition[]);

  const [showCreateForm, setShowCreateForm] = useState(false);
  const [showDeletePopup, setShowDeletePopup] = useState(false);
  const [editionToDelete, setEditionToDelete] = useState('');
  const [error, setError] = useState('');
  const [retry, setRetry] = useState(false);

  const axiosAuth = useAxiosAuth();
  let controller = new AbortController();

  useEffect(() => {
    controller.abort();
    controller = new AbortController();
    const signal = controller.signal;
    (async () => {
      await getAllEditions(signal);
    })();
    return () => {
      controller.abort();
    };
  }, []);

  /**
   * When page is reloaded, first request will fail with 401, retry once
   */
  useEffect(() => {
    if (!retry) {
      return;
    }
    controller.abort();
    controller = new AbortController();
    const signal = controller.signal;
    (async () => {
      await getAllEditions(signal);
    })();
    return () => {
      controller.abort();
    };
  }, [retry]);

  const getAllEditions = async (signal: AbortSignal) => {
    try {
      const activeResponse = await axiosAuth.get(
        Endpoints.EDITIONS + '/active',
        { signal: signal }
      );
      const inactiveResponse = await axiosAuth.get(
        Endpoints.EDITIONS + '/inactive',
        { signal: signal }
      );

      const allEditionsList: Edition[] = inactiveResponse.data;

      if (activeResponse.data) {
        allEditionsList.push(activeResponse.data);
      }

      setAllEditions(allEditionsList);
    } catch (err: unknown) {
      parseError(err, setError, router, signal);
      setRetry(true);
    }
  };

  const createEdition = async (_edition: string) => {
    try {
      await axiosAuth.post(Endpoints.EDITIONS, _edition);
      setAllEditions([{ name: _edition, isActive: false }, ...allEditions]);
    } catch (err) {
      parseError(err, setError, router);
    }
  };

  const updateEdition = (_edition: string) => {
    setEdition(_edition);
    localStorage.setItem('edition', _edition);
    router.push(`/${_edition}/projects`);
  };

  const deleteEdition = async (_edition: string) => {
    if (!_edition) return;

    try {
      await axiosAuth.delete(Endpoints.EDITIONS + `/${_edition}`);
      setAllEditions(allEditions.filter((val) => val.name !== _edition));
    } catch (err) {
      parseError(err, setError, router);
    }
  };

  return (
    <PersistLogin>
      <RouteProtection allowedRoles={[UserRole.Admin]}>
        <Head>
          <title>Editions</title>
        </Head>
        <div className="min-w-screen flex min-h-screen">
          <Header setError={setError} />

          {error && (
            <Error
              error={error}
              className="mt-[200px] w-3/5 sm:mt-16"
              setError={setError}
            />
          )}

          <div className="row-auto m-auto mt-[200px] grid w-9/12 grid-cols-1 items-center gap-4 sm:mt-16 md:mt-12 md:grid-cols-2 lg:mt-20 lg:grid-cols-3 xl:grid-cols-4">
            {showCreateForm ? (
              <EditionCreateForm
                setShowCreateForm={setShowCreateForm}
                createEdition={createEdition}
              />
            ) : (
              <div
                className="m-auto max-w-sm hover:cursor-pointer"
                title="Create New Edition"
                onClick={() => setShowCreateForm(true)}
              >
                <PlusCircleIcon className="h-12 w-12" color="#d3d3d3" />
              </div>
            )}
            {allEditions
              .sort((ed1, ed2) => Number(ed2.isActive) - Number(ed1.isActive))
              .map((val: Edition, idx: number) => (
                <EditionCard
                  key={idx}
                  edition={val}
                  updateEdition={updateEdition}
                  deleteEdition={() => {
                    setShowDeletePopup(true);
                    setEditionToDelete(val.name);
                  }}
                />
              ))}
          </div>
          <EditionDeletionPopup
            deleteEdition={async () => await deleteEdition(editionToDelete)}
            openDeleteForm={showDeletePopup}
            setOpenDeleteForm={setShowDeletePopup}
          />
        </div>
      </RouteProtection>
    </PersistLogin>
  );
};

export default Editions;
