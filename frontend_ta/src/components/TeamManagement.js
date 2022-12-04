import { Component, useState } from "react";
import ListPlayers from "./ListPlayers";
import postData from '../utils';
import { getData } from "../utils";
import "../styles/teamManagement.css";

class ManageTeams extends Component {

  constructor(props) {
    super(props);
    this.state = {
      user: {},
      team: {},
      players: [],
      errorMessage: "",
      display_invite: false,
      isUserLoaded: false,
      isTeamLoaded1: false,
      isTeamLoaded: false,
      isPlayersLoaded: false
    };
    this.handleClickInvite = this.handleClickInvite.bind(this);
    this.handleClickLeave = this.handleClickLeave.bind(this);
    this.handleClickKick = this.handleClickKick.bind(this);

  }

  componentDidMount() {
    this.initUser();
    this.initTeam();
    this.initPlayers();

  }

  initUser() {
    let url_player = process.env.REACT_APP_BACKEND_ADDRESS + "/api/player/get"
    getData(url_player)
      .then((response) => {
        if (response.result) {
          this.setState({
            isUserLoaded: true,
            user: response.result
          })
        }
        else {
          this.setState({ errorMessage: "Error finding player for user" })
        }
      });
    this.setState({})
  }

  initTeam() {
    let url_team = process.env.REACT_APP_BACKEND_ADDRESS + "/api/team/get-mine"
    getData(url_team)
      .then((response) => {
        if (response.result) {
          this.setState({
            isTeamLoaded1: true,
            team: response.result
          })
        }
        else {
          this.setState({ errorMessage: "No teams for this user" })
        }
      });

  }

  initPlayers() {
    let url_player = process.env.REACT_APP_BACKEND_ADDRESS + "/api/player/get-all/"
    getData(url_player)
      .then((response) => {
        if (response.result) {
          this.setState({
            isPlayersLoaded: true,
            players: response.result
          })
        }
        else {
          this.setState({ errorMessage: "No player in tha team" })
        }
      });
  }

  renderErrorMessage() {
    if (this.state.errorMessage == null) return;
    return (
      <p>{this.state.errorMessage}</p>
    );
  }


  render() {
    return (
      <div className="main-panel">
        <h2>Team Management</h2>
        <div className="flex-container-main">
          {this.displayTeamInfo()}
          {this.invitations()}
        </div>
      </div>
    );
  }

  handleClickInvite() {
    const previous_value = this.state.display_invite
    this.setState({ display_invite: !previous_value })
  }

  handleClickLeave() {
    let url_leave = process.env.REACT_APP_BACKEND_ADDRESS + "/api/team/leave/"
    postData(url_leave)
      .then((response) => {
        if (response.result) {
          alert(`${this.state.user.username}, you left the team ${this.state.team.name}`);
        }
        else {
          this.setState({ errorMessage: "Error" })
        }
      });
  }

  handleClickKick(event) {
    let url_kick = process.env.REACT_APP_BACKEND_ADDRESS + "/api/team/kick_member/"
    let data = { idKickedPlayer: event.target.id };
    postData(url_kick, data)
      .then((response) => {
        if (response.result) {
          alert(`The player ${this.state.players.find(p => p.id = event.target.id).username} has been kicked from the team ${this.state.team.name}`);
        }
        else {
          this.setState({ errorMessage: "Error" })
        };
        
      });

  }

  displayTeamInfo() {
    if (this.state.isTeamLoaded1) {
      return (
        <div className="flex-items-main">
          <h3>Your team</h3>
          <div className="flex-container-btn">
            <div className="team-info flex-items-btn">
              <div className="flex-container-info">
                <span className="flex-items-info name-entry">Name:</span>
                <span className="flex-items-info">{this.state.team.name}</span>
              </div>
              <div className="flex-container-info">
                <span className="flex-items-info name-entry">Team policy:</span>
                <span className="flex-items-info">{this.state.team.policy}</span>
              </div>
              <div className="flex-container-info">
                <span className="flex-items-info name-entry">Date of creation:</span>
                <span className="flex-items-info">
                  {this.state.team.dateOfCreation.join('/')}
                </span>
              </div>
            </div>
            {this.actionInvite()}
          </div>
          {this.displayTeamMember()}
        </div>
      )
    }
  }

  displayTeamMember() {
    // to erase after fix pb teamMember back
    console.log('ici')
    console.log(this.state.isTeamLoaded1 )
    console.log(this.state.isTeamLoaded)
    if (this.state.isTeamLoaded1 && !this.state.isTeamLoaded) {
      console.log('ici')

      let teamMember = []
      this.state.team.teamMembers.forEach(element => {
        if (element.username === undefined) {
          teamMember.push(element);
        } else {
          teamMember.push(element.username)
        }

      });
      let newteam = this.state.team;
      newteam.teamMembers = teamMember;
      this.setState({ team: newteam })
      this.setState({
        isTeamLoaded: true
      })

    }

    if (this.state.isPlayersLoaded) {
      return (
        <table>
          <thead>
            <tr>
              <th>Player</th>
              <th>Role</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr></tr>
            {this.state.team.teamMembers.map(player =>
              <tr id={this.state.players.find(p => p.username == player).id}>
                <td>{player}</td>
                <td>{this.state.players.find(p => p.username == player).role}</td>
                <td>
                  {this.actionPlayer(this.state.players.find(p => p.username == player).id)}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      )
    }
  }

  actionPlayer(idPlayer) {
    if (this.state.isUserLoaded) {
      if (idPlayer === this.state.user.id && this.state.user.role !== "LEADER") {
        return (<div class="btn btn-red" name="leave" id={idPlayer} onClick={this.handleClickLeave} >Leave</div>);
      } else if (idPlayer !== this.state.user.id && this.state.user.role === "LEADER") {
        return (<div class="btn btn-red" name="kick" id={idPlayer} onClick={this.handleClickKick} >Kick from the team</div>);
      } else return;
    }
  }

  actionInvite() {
    if (this.state.isUserLoaded) {
      if (this.state.user.role === "LEADER") {
        return (
          <div class="btn btn-yellow flex-items-btn" name="display_invite" onClick={this.handleClickInvite}>Invite players</div>
        );
      } else return;
    }
  }

  handleSendInvite(event) {
    let url_invit = process.env.REACT_APP_BACKEND_ADDRESS + "/api/team/invit/"
    let data = { id: event.target.id };
    postData(url_invit, data)
      .then((response) => {
        if (response.result) {
          alert(`An invitation has been send to the player ${event.target.id} to join the team`);
          this.setState({ errorMessage: "Invitation has been send" })
        }
        else {
          alert(`Error while sending an invitation to the player ${event.target.id} to join the team`);
          this.setState({ errorMessage: "Problem in sending invitation" })
        }
      });
  }

  invitations() {
    if (this.state.display_invite && this.state.isPlayersLoaded) {
      return (
        <div className="flex-items-main">
          <h3>Send invitation to join the team</h3>
          <ListPlayers
            players={this.state.players
              .filter(user => !this.state.team.teamMembers.find(p => p === user.username))
            }
            handleClick={event => this.handleSendInvite(event)}
            btnName="invite" />
        </div>
      )
    }
    else return
  }
}

export default ManageTeams;