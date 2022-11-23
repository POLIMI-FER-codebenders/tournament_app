import { Component, useState } from "react";
import ListPlayers from "./ListPlayers";
import postData from '../utils';


class ManageTeams extends Component {

  constructor(props) {
    super(props);
    this.state = {
      user: { "id": 1, "name": "Fanny", "role": "Leader", "id_team": 1 },
      team: this.initTeam(),
      players: this.initPlayers(),
      errorMessage: "",
      edit: []
    };
  }

  initTeam() {
    const data_team =
    {
      "id": 1,
      "name": "Steaming hot coffee enjoyers",
      "date": "16.10.2022",
      "type": "close"
    };
    // let url_team = "http://localhost:8080/api/team/get/"
    // let dt = { id: this.state.user.id_team };
    // postData(url_team, dt)
    //   .then((response) => {
    //     if (response.result) {
    //       this.setState({ team: response.result })
    //     }
    //     else {
    //       this.setState({ errorMessage: "No teams for this user" })
    //     }
    //   });
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
    return (data_players )

  }

  renderErrorMessage() {
    if (this.state.errorMessage == null) return;
    return (
      <p>{this.state.errorMessage}</p>
    );
  }


  render() {
    return (
      <div class="main-panel">
        <h2>Team Management</h2>
        {this.displayTeamInfo()}
        {this.invitations()}
      </div>
    );
  }

  displayTeamInfo() {
    
    return (
      <div className="team_details">
        <h3>Your team</h3>
        <div>Name</div>
        <div>{this.state.team.name}</div>
        <div>Type</div>
        <div>{this.state.team.type}</div>
        <div>Date</div>
        <div>{this.state.team.date}</div>
        
        <table>
          <thead>
            <tr>
              <th>Player</th>
              <th>Role</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {this.state.players.map((player) =>
              <tr>
                <td>{player.name}</td>
                <td>{player.role}</td>
                <td>
                  <div class="btn btn-red" name="kick" id={player.id}>Kick from the team</div>
                </td>
              </tr>
            )}
          </tbody>
        </table>

      </div>
    )
  }


  handleSendInvit(id) {
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
    return (
      <div className="invit">
        <h3>Send invitation to join the team</h3>
        <ListPlayers players={this.state.players} handleClick={(event, id) => this.handleSendInvit(event, id)} btnName="invit"/>
      </div>
    )
  }
}

export default ManageTeams;