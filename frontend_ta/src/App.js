import logo from './logo.svg';
import './styles/App.css';
import './styles/forms.css';
import { Component } from "react";
import SignIn from './components/SignIn';
import { CreateTeam } from './components/TeamManagement';
import { DisplayTournament } from './components/DisplayTournament';
import ManageTeams from './components/TeamManagement';
import ErrorPage from "./Error.js";
import React from "react";
import ReactDOM from "react-dom/client";
import { useState } from 'react';
import {
  createBrowserRouter,
  RouterProvider,
  Route,
} from "react-router-dom";

function Header() {
  return (
    <header className="App-header">
      <p>Welcome to tournament application of Code Defenders web game!</p>
    </header>
  );
}

function MainPanel(props) {
  switch (props.view) {
    case 0:
      return <DisplayTournament />;
    case 1:
      return <SignIn backHome={props.backHome} />
    case 2:
      return <CreateTeam />;
    case 3:
      return <ManageTeams />;
    default:
      return <DisplayTournament />;
  }
}
function Home(props) {
  const [view, setView] = useState(0);
  function backHome() {
    setView(0);
  }
  return (
    <div>
      <Header />
      <div class="main-container">
        <div class="button-container">
          <button class="item"
            onClick={() => setView(0)}>
            Home
          </button>
          <button class="item"
            onClick={() => setView(1)}>
            Sign In
          </button>
          <button class="item"
            onClick={() => setView(2)}>
            Create Team
          </button>
          <button class="item"
            onClick={() => setView(3)}>
            Manage Teams
          </button>
        </div>
        <MainPanel view={view} backHome={backHome} />
        <div class="item"></div>
      </div>
    </div>
  );
}

class App extends Component {
  constructor(props) {
    super(props);
  }

  router = createBrowserRouter([
    {
      path: "/",
      element: < Home backHome={this.backHome} />,
      errorElement: <ErrorPage />,
    },
  ]);
  render() {
    return (
      <React.StrictMode>
        <RouterProvider router={this.router} />
      </React.StrictMode>
    );
  }
}
export default App;
