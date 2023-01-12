import logo from './logo.svg';
import './styles/App.css';
import './styles/forms.css';
import { Component } from "react";
import SignIn from './components/SignIn';
import { CreateTeam } from './components/TeamManagement';
import { DisplayTournament } from './components/DisplayTournament';
import ManageTeams from './components/TeamManagement';
import { CreateTournament } from './components/TournamentCreation';
import TeamCreation from './components/TeamCreation';
import JoinTeam from './components/JoinTeam';
import { CDFrame } from './components/CDFrame';
import cdlogo from './images/cdlogo.png';
import ErrorPage from "./Error.js";
import React from "react";
import ReactDOM from "react-dom/client";
import { useState } from 'react';
import {
  createBrowserRouter,
  RouterProvider,
  Route,
} from "react-router-dom";
import Streaming from './components/Streaming';




function MainPanel(props) {
  switch (props.view) {
    case 0:
      return <DisplayTournament backHome ={props.backHome} index={1} />;
    case 1:
      return <SignIn backHome={props.backHome} index={0} />
    case 2:
      if (sessionStorage.getItem("username") != null) return <TeamCreation />;
      else return <SignIn backHome={props.backHome} index={2} />
    case 3:
      if (sessionStorage.getItem("username") != null) return <ManageTeams />;
      else return <SignIn backHome={props.backHome} index={3} />
    case 4:
      if (sessionStorage.getItem("username") != null) return <JoinTeam />;
      else return <SignIn backHome={props.backHome} index={4} />
    case 5: 
    if (sessionStorage.getItem("username") != null) return <CreateTournament />;
      else return <SignIn backHome={props.backHome} index={5} />
    default:
      return <DisplayTournament backHome ={props.backHome} index={1} />;
  }
}
class Home extends React.Component {
   

  constructor(props) {
    super(props);
    let buttontext ;
    if(sessionStorage.getItem("username")!=null)buttontext="Logout"
    else buttontext="SignIn"
    this.state = {
      view: 0,
      buttontext: buttontext,
      error:false
    };
    this.backHome = this.backHome.bind(this);
    this.Header=this.Header.bind(this);
  }
  Header() {
    let buttontext;
    if(sessionStorage.getItem("username")!=null)buttontext="Logout"
    else buttontext="SignIn"
    return (
      <div className="App-header">
        <img id="cdlogoheader" src={cdlogo}></img>
        Hello{sessionStorage.getItem("username")&&" "+sessionStorage.getItem("username")}! Welcome to tournament application of Code Defenders web game!
        <div id="signindiv" >
        <button class="signinlink"
              onClick={() => 
                {
              if(sessionStorage.getItem("username")!=null) {
                 sessionStorage.removeItem("username");
                fetch(process.env.REACT_APP_BACKEND_ADDRESS + "/authentication/logout").then(response => {
                 if(response.status!=200){
                   console.log("error");
                 }
                                          
                });
                this.setState({ view: 1,buttontext:"SignIn" });
              }
               else this.setState({ view: 1 })}
                }
              >
              {this.state.buttontext}
            </button> 
            </div>
       </div>
    );
  }
  backHome(index,loggedin) {
    if(loggedin)this.setState({ view: index,buttontext:"Logout" });
    else this.setState({ view: index });
    
  }
  render() {
    return (
      <div>
        {this.Header()}
        <div class="main-container" id="mainpagebackground">
          <div id="sidebar">
          <div  id="mainbuttoncontainer">
            <button class="firstitem barbutton"
              onClick={() => this.setState({ view: 0 })}>
              Home
            </button>
            
            <button class="item barbutton"
              onClick={() => this.setState({ view: 2 })}>
              Create Team
            </button>
            <button class="item barbutton"
              onClick={() => this.setState({ view: 3 })}>
              Manage Teams
            </button>
            <button class="item barbutton" 
              onClick={() => this.setState({ view: 4 })}>
              Join Team
            </button>
            <button class="item barbutton" 
              onClick={() => this.setState({ view: 5 })}>
              Tournament Creation
            </button>
          </div>
          </div>
          <MainPanel view={this.state.view} backHome={this.backHome} />
          
        </div>
      </div>
    );
  }
}

class App extends Component {
  constructor(props) {
    super(props);
  }

  router = createBrowserRouter([
    {
      path: "/",
      element: < Home />,
      errorElement: <ErrorPage />,
    },
    {
      path: "/error",
      element: <ErrorPage />
    },
    {
      path: "/play",
      element:<CDFrame />
    },
    {
      path:"/streaming",
      element:<Streaming/>
    }

  ]

  );
  render() {
    return (
      <RouterProvider router={this.router} />
    );
  }
}
export default App;
