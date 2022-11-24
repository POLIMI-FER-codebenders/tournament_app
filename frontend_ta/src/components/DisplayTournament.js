import React from 'react';
import '../styles/App.css';
import ListTournamentComponent from './ListTournamentComponent';

export class DisplayTournament extends React.Component {
  constructor(props) {
    super(props);

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