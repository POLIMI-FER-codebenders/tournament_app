import React from 'react';
import '../styles/App.css';
import ListTournamentComponent from './ListTournamentComponent';
import {getData} from "../utils.js"

export class DisplayTournament extends React.Component {
  constructor(props) {
    super(props);

  }
  componentDidMount() {
    getData("/api/tournament/list").then((response)=> {
     if (response.status == 200) {
      console.log(response.result);
     }
     else console.log("error");
    }
    );
  }
  render() {
    const data = [
      { "name": "the code league", "date": "16.10.2022", "status": "started","type":"league","teamsize":8 },
      { "name": "cr tour", "date": "14.10.2022", "status": "not started","type":"knockout","teamsize":12},
      { "name": "Chunin tour ", "date": "14.10.2022", "status": "started","type":"knockout","teamsize":4 }
    ];
    return (

      <div class="main-panel">
        <h2>Tournaments</h2>
        <ListTournamentComponent tournaments={data} />
      </div>
    );
  }

}