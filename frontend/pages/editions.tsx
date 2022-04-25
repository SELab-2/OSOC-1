import { NextPage } from 'next';
import EditionCard from '../components/editions/EditionCard';
import Header from '../components/Header';
import { PlusCircleIcon } from '@heroicons/react/outline';
import { useRouter } from 'next/router';
import useEdition from '../hooks/useEdition';
import axios from 'axios';
import useAxiosAuth from '../hooks/useAxiosAuth';
import Endpoints from '../lib/endpoints';
import { useEffect, useState } from 'react';
import EditionCreateForm from '../components/editions/EditionCreateForm';
import Error from '../components/Error';
import { Edition } from '../lib/types';

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
  const [error, setError] = useState('');

  const axiosAuth = useAxiosAuth();

  useEffect(() => {
    const getAllEditions = async () => {
      try {
        const activeResponse = await axiosAuth.get(
          Endpoints.EDITIONS + '/active'
        );
        const inactiveResponse = await axiosAuth.get(
          Endpoints.EDITIONS + '/inactive'
        );

        const allEditionsList: Edition[] = inactiveResponse.data;

        if (activeResponse.data) {
          allEditionsList.push(activeResponse.data);
        }

        setAllEditions(allEditionsList);
      } catch (err: unknown) {
        if (axios.isAxiosError(err)) {
          const status = err.response?.status;
          if (status === 400) router.push('/login');
          setError(err.message);
        } else {
          setError(err as string);
        }
      }
    };

    getAllEditions();
  }, []);

  const createEdition = async (_edition: string) => {
    try {
      await axiosAuth.post(Endpoints.EDITIONS, _edition);
      setAllEditions([{ name: _edition, isActive: false }, ...allEditions]);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const status = err.response?.status;
        if (status === 400) router.push('/login');
        setError(err.message);
      } else {
        setError(err as string);
      }
    }
  };

  const updateEdition = (_edition: string) => {
    setEdition(_edition);
    router.push(''); // TODO. update this when students/projects page has been added
    console.log(_edition);
  };

  const deleteEdition = async (_edition: string) => {
    try {
      await axiosAuth.delete(Endpoints.EDITIONS + `/${_edition}`);
      setAllEditions(allEditions.filter((val) => val.name !== _edition));
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const status = err.response?.status;
        if (status === 400) router.push('/login');
        setError(err.message);
      } else {
        setError(err as string);
      }
    }
  };

  return (
    <div className="h-screen">
      <Header />

      {error && <Error error={error} className="mt-4 w-3/5" />}

      <div className="row-auto m-auto mt-4 grid w-9/12 grid-cols-1 items-center gap-4 md:mt-8 md:grid-cols-2 lg:mt-12 lg:grid-cols-3 xl:grid-cols-4">
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
              deleteEdition={deleteEdition}
            />
          ))}
      </div>
    </div>
  );
};

export default Editions;
