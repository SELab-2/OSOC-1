import type { NextPage } from 'next';
import Header from '../../components/Header';
import StudentSidebar from '../../components/StudentSidebar';
import { Icon } from '@iconify/react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { useEffect, useState } from 'react';
import {
  ProjectBase,
  ProjectData,
  StudentBase,
  UserRole,
} from '../../lib/types';
import { axiosAuthenticated } from '../../lib/axios';
import Endpoints from '../../lib/endpoints';
import useAxiosAuth from '../../hooks/useAxiosAuth';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import Popup from 'reactjs-popup';
import ProjectTile from '../../components/projects/ProjectTile';
import ProjectPopup, {
  defaultprojectForm,
} from '../../components/projects/ProjectPopup';
import FlatList from 'flatlist-react';
import useUser from '../../hooks/useUser';
import { SpinnerCircular } from 'spinners-react';
import Error from '../../components/Error';
import { parseError } from '../../lib/requestUtils';
import RouteProtection from '../../components/RouteProtection';
import { useRouter } from 'next/router';
import { NextRouter } from 'next/dist/client/router';

const ResetPassword: NextPage = () => {
  const router = useRouter();
  const token = router.query.resetPasswordToken as string;

  return (
    <div>{token}</div>
  );
};

export default ResetPassword;
