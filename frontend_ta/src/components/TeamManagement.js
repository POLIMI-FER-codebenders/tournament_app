import { Component, useState } from "react";
import ListPlayers from "./ListPlayers";
import postData from '../utils';
import { getData } from "../utils";
import "../styles/teamManagement.css";

class ManageTeams extends Component {

  constructor(props) {
    super(props);
    this.state = {
      user: { "id": 1, "name": "Fanny", "role": "Leader", "id_team": 1 },
      team: this.initTeam(),
      players: this.initPlayers(),
      errorMessage: "",
      display_invit: false
    };
    this.handleClickInvit = this.handleClickInvit.bind(this);
    this.handleClickLeave = this.handleClickLeave.bind(this);
    this.handleClickKick = this.handleClickKick.bind(this);

  }

  initTeam() {
    const data_team =
    {
      "id": 1,
      "name": "Steaming hot coffee enjoyers",
      "date": "16.10.2022",
      "type": "Closed"
    };
     let url_team = "/api/team/get-mine"
     getData(url_team)
       .then((response) => {
         if (response.result) {
           this.setState({ team: response.result })
         }
         else {
           this.setState({ errorMessage: "No teams for this user" })
         }
       });
    return (data_team)
  }
  initPlayers() {
    const data_players = [
      { "id": 1, "name": "Fanny", "role": "Leader", "score": 10 },
      { "id": 2, "name": "ric", "role": "Member", "score": 11 },
      { "id": 3, "name": "Vrganj", "role": "Member", "score": 12 },
      { "id": 4, "name": "Simon99", "role": "Member", "score": 13 }
    ]
    // let url_player = "http://localhost:8080/api/players/get/"
    // let dp = { id: this.state.user.id_team };
    // postData(url_player, dp)
    //   .then((response) => {
    //     if (response.result) {
    //       this.setState({ players: response.result })
    //     }
    //     else {
    //       this.setState({ errorMessage: "No player in tha team" })
    //     }
    //   });
    return (data_players)

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

  handleClickInvit() {
    const previous_value = this.state.display_invit
    this.setState({ display_invit: !previous_value })
  }

  handleClickLeave() {
    // let url_leave = "http://localhost:8080/api/team/leave/"
    // let data = { id: this.state.user.id };
    // postData(url_leave, data)
    //   .then((response) => {
    //     if (response.result) {
    //       this.setState({ errorMessage: "Leaving the team" })
    //     }
    //     else {
    //       this.setState({ errorMessage: "Error" })
    //     }
    //   });
  }

  handleClickKick(event) {
    // let url_kick = "http://localhost:8080/api/team/kick/"
    // let data = { id: event.target.id };
    // postData(url_kick, data)
    //   .then((response) => {
    //     if (response.result) {
    //       this.setState({ errorMessage: "Kick player from the team" })
    //     }
    //     else {
    //       this.setState({ errorMessage: "Error" })
    //     }
    //   });
  }

  displayTeamInfo() {

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
              <span className="flex-items-info">{this.state.team.policy}</span>
            </div>
          </div>
          {this.actionInvit()}
        </div>

        <table>
          <thead>
            <tr>
              <th>Player</th>
              <th>Role</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {this.state.players.map((player, i) =>
              <tr id={i}>
                <td>{player.name}</td>
                <td>{player.role}</td>
                <td>
                  {this.actionPlayer(player.id)}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    )
  }

  actionPlayer(idPlayer) {
    if (idPlayer === this.state.user.id) {
      return (<div class="btn btn-red" name="leave" id={idPlayer} onClick={this.handleClickLeave} >Leave</div>);
    } else if (this.state.user.role === "Leader"){
      return (<div class="btn btn-red" name="kick" id={idPlayer} onClick={this.handleClickKick} >Kick from the team</div>);
    } else return;
  }

  actionInvit() {
    if (this.state.user.role === "Leader"){
      return (
        <div class="btn btn-yellow flex-items-btn" name="display_invit" onClick={this.handleClickInvit}>Invite players</div>
      );
    } else return;
  }

  handleSendInvit(event, id) {
    // let url_invit = "http://localhost:8080/api/team/invit/"
    // let data = { id: id };
    // postData(url_invit, data)
    //   .then((response) => {
    //     if (response.result) {
    //       this.setState({ errorMessage: "Invitation has been send" })
    //     }
    //     else {
    //       this.setState({ errorMessage: "Problem in sending invitation" })
    //     }
    //   });
  }

  invitations() {
    if (this.state.display_invit) {
      return (
        <div className="flex-items-main">
          <h3>Send invitation to join the team</h3>
          <ListPlayers players={this.state.players} handleClick={(event, id) => this.handleSendInvit(event, id)} btnName="invit" />
        </div>
      )
    }
    else return
  }
}

export default ManageTeams;