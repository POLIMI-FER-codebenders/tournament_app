import { Component } from "react";
// import "../styles/listPlayers.css";
import "../styles/teamManagement.css";

class ListPlayers extends Component {
  constructor(props) {
    super(props);
    this.state = {
      players: props.players,
      handleClick: props.handleClick,
      btn_name: props.btn_name
    };
  }


  render() {
    return (
      <div class="list">
        <div class="list-headers flex-container-list">
          <div class="col1 flex-items-list">Name</div>
          <div class="col2 flex-items-list">Score</div>
          <div class="col3 flex-items-list"></div>
        </div>
        {this.state.players.map((object, i) => 
          <PlayerEntry obj={object} key={i} handleClick={event => this.state.handleClick(event)} btn_name={this.state.btn_name}/>
        )}
      </div>
    );

  }
}

function PlayerEntry(props) {
  return (
    <div class="list-entry flex-container-list">
      <div class="col1 flex-items-list">{props.obj.username}</div>
      <div class="col2 flex-items-list">{props.obj.score}</div>
      <div class="col3 flex-items-list">
        <div class="btn btn-green" id={props.obj.id} onClick={props.handleClick}>Send invitation</div>
      </div>
    </div>
  );
}


export default ListPlayers;