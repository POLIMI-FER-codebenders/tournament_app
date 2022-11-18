import React from 'react';
import '../styles/App.css';
import ListTournamentComponent from './ListTournamentComponent';

export class DisplayTournament extends React.Component {
  constructor(props) {
    super(props);
    
  }
  render() {
    const data = [
      { "name": "Steaming hot coffee enjoyers", "date": "16.10.2022", "status": "started" },
      { "name": "Guys from the basement", "date": "14.10.2022", "status": "not started" }
    ];
    return (

      <div class="main-panel">
        <h2>Tournaments</h2>
        <ListTournamentComponent tournaments={data} />
      </div>
    );
  }

}