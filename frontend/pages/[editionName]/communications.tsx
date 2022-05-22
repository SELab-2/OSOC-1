import { useRouter } from 'next/router';
import RouteProtection from '../../components/RouteProtection';
import {
  Communication,
  CommunicationType,
  StudentBaseCommunication,
  StudentComm,
  StudentDataCommunication,
  UserRole,
} from '../../lib/types';
import CommsTable from '../../components/communications/CommsTable';
import { useEffect, useState } from 'react';
import Header from '../../components/Header';
import { SpinnerCircular } from 'spinners-react';
import Error from '../../components/Error';
import useAxiosAuth from '../../hooks/useAxiosAuth';
import Endpoints from '../../lib/endpoints';
import {fetchEditionState, getUrlList, parseError} from '../../lib/requestUtils';
import CommsCreationPopup from '../../components/communications/CommsCreationPopup';
import CsvDownloader from 'react-csv-downloader';
import PersistLogin from '../../components/PersistLogin';
import CommsDeletePopup from '../../components/communications/CommsDeletePopup';
import Head from 'next/head';

const PAGE_SIZE = 50;

const communications = () => {
  const router = useRouter();
  const { editionName: edition } = router.query;

  const [students, setStudents] = useState([] as StudentBaseCommunication[]);
  const [communications, setCommunications] = useState([] as StudentComm[]);
  const [filteredCommunications, setFilteredCommunications] = useState(
    [] as StudentComm[]
  );

  const [openPopup, setOpenPopup] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [openDeletionPopup, setOpenDeletionPopup] = useState(false);
  const [commsToDelete, setCommsToDelete] = useState('');
  const [nameFilter, setNameFilter] = useState('');

  const [loadState, setLoadState] = useState({
    page: 0,
    hasMore: true,
  });
  const [editionActive, setEditionActive] = useState(true);

  const axiosAuth = useAxiosAuth();
  const [retry, setRetry] = useState(true);
  let controller = new AbortController();

  fetchEditionState(setEditionActive, setError, router);

  /**
   * Fetches students and their communications and updates the state of the application accordingly.
   *
   * @param page - page number to browse
   * @param abortController - abort controller to check if the request has been aborted.
   */
  const getStudentsAndComms: ({
    page,
    signal: AbortSignal,
  }: {
    page: number;
    signal: AbortSignal;
  }) => Promise<void> = async ({ page, signal }) => {
    try {
      const response = await axiosAuth.get<StudentDataCommunication>(
        `/${edition}` + Endpoints.STUDENTS,
        {
          params: {
            pageSize: PAGE_SIZE,
            pageNumber: page,
            view: 'Communication',
            sortBy: 'firstName',
          },
          signal: signal,
        }
      );

      if (signal.aborted) return Promise.reject();

      const _students = response.data.collection;
      const studentComms = [] as StudentComm[];
      for (const student of _students) {
        // check if the student has any communications already
        const _comms = student.communications;
        if (_comms.length > 0) {
          const curStudentComms = [] as Communication[];
          await getUrlList(_comms, curStudentComms, signal, setError, router);
          curStudentComms.forEach((csc) => {
            studentComms.push({
              studentId: student.id,
              name: student.firstName + ' ' + student.lastName,
              commMessage: csc.message,
              registrationTime: new Date(csc.registrationTime),
              id: csc.id,
            });
          });
        }
      }

      setStudents([...students, ..._students]);
      setCommunications([...communications, ...studentComms]);
      setLoadState((prev) => {
        return {
          page: prev.page + 1,
          hasMore: response.data.totalLength > prev.page * PAGE_SIZE,
        };
      });
      setRetry(true);
    } catch (err) {
      parseError(err, setError, router, signal);
      if (retry) {
        setRetry(false);
        setLoadState((prev) => {
          return {
            page: prev.page,
            hasMore: true,
          };
        });
      }
    }
  };

  /**
   * Keep loading new data as long as the loadState states that there is more data to be fetched
   */
  useEffect(() => {
    if (!router.isReady) {
      return;
    }

    if (!loadState.hasMore) {
      setLoading(false);
      return;
    }
    controller.abort();
    controller = new AbortController();
    const signal = controller.signal;

    (async () => {
      await getStudentsAndComms({ page: loadState.page, signal });
    })();
    return () => {
      controller.abort();
    };
  }, [loadState, router.isReady]);

  /**
   * Update the communications to only include the name filtered communications
   */
  useEffect(() => {
    const filterCommunications: () => StudentComm[] = () => {
      const normalizedNameFilter = nameFilter.trim().toLowerCase();
      return communications.filter((comm: StudentComm) => {
        return comm.name.trim().toLowerCase().includes(normalizedNameFilter);
      });
    };

    if (nameFilter) {
      const _filteredCommunications = filterCommunications();
      setFilteredCommunications(_filteredCommunications);
    } else {
      setFilteredCommunications(communications);
    }
  }, [communications, nameFilter]);

  const createCommunication = async (studentId: string, message: string) => {
    try {
      const response = await axiosAuth.post<Communication>(
        `/${edition}` + Endpoints.COMMS + `/${studentId}`,
        JSON.stringify({
          message,
          type: CommunicationType.Email,
        })
      );

      // add new communication locally
      const _stud = students.find((stud) => stud.id === studentId);
      const name = _stud ? _stud.firstName + ' ' + _stud.lastName : '';
      setCommunications((prev) => {
        const newComms = [
          ...prev,
          {
            studentId: studentId,
            name,
            commMessage: message,
            registrationTime: new Date(response.data.registrationTime),
            id: response.data.id,
          } as StudentComm,
        ];
        newComms.sort((a, b) => (a.name >= b.name ? 1 : -1));
        return newComms;
      });
    } catch (err) {
      parseError(err, setError, router);
    }
  };

  const deleteCommunication = async (communicationId: string) => {
    if (!communicationId) return;

    try {
      await axiosAuth.delete(
        `/${edition}${Endpoints.COMMS}/${communicationId}`
      );
      setCommunications((prev) => {
        return prev.filter((comm) => comm.id !== communicationId);
      });
    } catch (err) {
      parseError(err, setError, router);
    }
  };

  return (
    <PersistLogin>
      <RouteProtection allowedRoles={[UserRole.Admin, UserRole.Coach]}>
        <Head>
          <title>{edition}: communication</title>
        </Head>
        <div className="min-w-screen flex min-h-screen">
          <Header setError={setError} />
          <div className="mx-auto mt-[200px] mb-32 w-11/12 p-0 sm:mt-16 lg:w-5/6 xl:w-4/5 xl1600:w-3/5">
            {error && (
              <Error error={error} className="mb-4" setError={setError} />
            )}
            <div>
              <button
                className="mx-2 my-1 rounded-sm bg-osoc-btn-primary px-2 py-1 text-black hover:brightness-95 disabled:brightness-75 disabled:cursor-not-allowed"
                onClick={() => setOpenPopup(true)}
                disabled={!editionActive}
              >
                Add New
              </button>
              <CsvDownloader
                datas={filteredCommunications.map((comm) => {
                  return {
                    id: comm.id,
                    name: comm.name,
                    message: comm.commMessage,
                    timestamp: comm.registrationTime.toString(),
                  };
                })}
                filename="communications"
                suffix
                className="inline-block cursor-default"
                disabled={filteredCommunications.length === 0}
                columns={[
                  {
                    id: 'id',
                    displayName: 'Id',
                  },
                  {
                    id: 'name',
                    displayName: 'Student name',
                  },
                  {
                    id: 'message',
                    displayName: 'Communication info',
                  },
                  {
                    id: 'timestamp',
                    displayName: 'Time',
                  },
                ]}
              >
                <button
                  className="my-1 mr-2 rounded-sm bg-osoc-yellow px-2 py-1 text-white hover:brightness-95 disabled:cursor-not-allowed disabled:bg-yellow-300 disabled:text-gray-200 disabled:brightness-100"
                  disabled={filteredCommunications.length === 0}
                >
                  Download CSV
                </button>
              </CsvDownloader>
            </div>
            <CommsTable
              studentComms={filteredCommunications}
              setCommsToDelete={setCommsToDelete}
              setShowDeleteForm={setOpenDeletionPopup}
              nameFilter={nameFilter}
              setNameFilter={setNameFilter}
            />
            {loading && filteredCommunications.length ? (
              <div className="">
                <SpinnerCircular
                  size={100}
                  thickness={80}
                  color="#FCB70F"
                  secondaryColor="rgba(252, 183, 15, 0.4)"
                  className="mx-auto"
                />
              </div>
            ) : null}
          </div>
        </div>
        <CommsCreationPopup
          createComms={createCommunication}
          openPopup={openPopup}
          setOpenPopup={setOpenPopup}
          students={students}
        />
        <CommsDeletePopup
          deleteComms={() => deleteCommunication(commsToDelete)}
          openDeleteForm={openDeletionPopup}
          setOpenDeleteForm={setOpenDeletionPopup}
          setCommsToDelete={setCommsToDelete}
        />
      </RouteProtection>
    </PersistLogin>
  );
};
export default communications;
