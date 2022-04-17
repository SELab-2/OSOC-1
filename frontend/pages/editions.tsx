import { NextPage } from "next";
import EditionCard from "../components/editions/EditionCard";
import Header from "../components/Header";
import { PlusCircleIcon } from "@heroicons/react/outline";
import { useRouter } from "next/router";
import useEdition from "../hooks/useEdition";
import axios from "axios";
import useAxiosAuth from "../hooks/useAxiosAuth";
import Endpoints from "../lib/endpoints";
import { useState } from "react";
import EditionCreateForm from "../components/editions/EditionCreateForm";

export type Edition = {
  name: string;
  isActive: boolean;
}

const editions: Edition[] = [
  {
    name: 'OSOC2018',
    isActive: false
  },
  {
    name: 'OSOC2019',
    isActive: false
  },
  {
    name: 'OSOC2020',
    isActive: false
  },
  {
    name: 'OSOC2021',
    isActive: false
  },
  {
    name: 'OSOC2022',
    isActive: true
  }
]

const Editions: NextPage = () => {
  const router = useRouter();
  const [allEditions, setAllEditions] = useState(editions);
  const [edition, setEdition] = useEdition();

  const [showCreateForm, setShowCreateForm] = useState(false);

  const axiosAuth = useAxiosAuth();

  const createEdition = async (_edition: string) => {
    try {
      // await axiosAuth.post()
      setAllEditions([{ name: _edition, isActive: false }, ...allEditions]);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const status = err.response?.status;
        if (status === 400) router.push('/login');
        console.error(err);
      }
    }
  }

  const updateEdition = (_edition: string) => {
    setEdition(_edition);
    router.push('');
    console.log(_edition);
  }

  const deleteEdition = async (_edition: string) => {
    try {
      // await axiosAuth.delete(Endpoints.EDITIONS);
      setAllEditions(allEditions.filter((val) => val.name !== _edition));
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const status = err.response?.status;
        if (status === 400) router.push('/login');
        console.error(err);
      }
    }
  }

  return (
    <div className="h-screen">
      <Header/>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 row-auto w-9/12 m-auto items-center gap-4 mt-4 md:mt-8 lg:mt-12">
        {
          showCreateForm
          ? (
            <EditionCreateForm
              setShowCreateForm={setShowCreateForm}
              createEdition={createEdition}
            />
          )
          : (
            <div className="max-w-sm m-auto hover:cursor-pointer" title="Create New Edition" onClick={() => setShowCreateForm(true)}>
              <PlusCircleIcon className="w-12 h-12" color="#d3d3d3"/>
            </div>
          )
        }
        {
          allEditions.map((val: Edition, idx: number) => <EditionCard key={idx} edition={val} updateEdition={updateEdition} deleteEdition={deleteEdition} />)
        }
      </div>
    </div>
  );
}

export default Editions;