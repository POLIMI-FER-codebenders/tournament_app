import React from 'react';
import '../styles/App.css';
import ListTournamentComponent from './ListTournamentComponent';
import {getData} from "../utils.js"

export class DisplayTournament extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      data:[]
    };

  }
  componentDidMount() {
    
    getData("/api/tournament/list").then((response)=> {
     if (response.status == 200) {
      this.setState({data: response.result});
     }
     else console.log("error");
    }
    );
  }
  render() {
    
    return (
      <div class="main-panel">
        <h2>Tournaments</h2>
        <ListTournamentComponent tournaments={this.state.data} backHome={this.props.backHome} index={this.props.index} />
      </div>
    );
  }

}