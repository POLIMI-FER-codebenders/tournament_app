import { Component } from "react";
// import "../styles/listPlayers.css";
import "../styles/teamManagement.css";

class ListPlayers extends Component {
  constructor(props) {
    super(props);
    this.state = {
      players_available: this.initPlayers(props.players),
      handleClick: props.handleClick,
      btn_name: props.btn_name
    };
  }

  initPlayers(players_team) {
  const data_players = [
    { "id": 1, "name": "Fanny", "role": "Leader", "score": 10 },
    { "id": 2, "name": "ric", "role": "Member", "score": 11 },
    { "id": 3, "name": "Vrganj", "role": "Member", "score": 12 },
    { "id": 4, "name": "Simon99", "role": "Member", "score": 13 },
    { "id": 5, "name": "Bob", "role": "Member", "score": 14 },
    { "id": 6, "name": "aaabb", "role": "Member", "score": 15 },
    { "id": 7, "name": "SanAndreas", "role": "Member", "score": 16 },
    { "id": 8, "name": "Hrvoje", "role": "Member", "score": 17 },
    { "id": 9, "name": "zzyy", "role": "Member", "score": 18 }
  ];
  // let url_player = "http://localhost:8080/api/user/get_all/"
  // postData(url_player)
  //   .then((response) => {
  //     if (response.result) {
  //       const data_players = response.result
  //     }
  //     else {
  //       this.setState({ errorMessage: "No player in tha team" })
  //     }
  //   });
  
  return (data_players.filter(user => !players_team.find(p => p.id === user.id)) );
  }

  render() {
    return (
      <div class="list">
        <div class="list-headers flex-container-list">
          <div class="col1 flex-items-list">Name</div>
          <div class="col2 flex-items-list">Score</div>
          <div class="col3 flex-items-list"></div>
        </div>
        {this.state.players_available.map((object, i) => 
          <PlayerEntry obj={object} key={i} handleClick={event => this.state.handleClick(event, i)} btn_name={this.state.btn_name}/>
        )}
      </div>
    );

  }
}

function PlayerEntry(props) {
  return (
    <div class="list-entry flex-container-list">
      <div class="col1 flex-items-list">{props.obj.name}</div>
      <div class="col2 flex-items-list">{props.obj.score}</div>
      <div class="col3 flex-items-list">
        <div class="btn btn-green" onClick={props.handleClick}>Send invitation</div>
      </div>
    </div>
  );
}


export default ListPlayers;