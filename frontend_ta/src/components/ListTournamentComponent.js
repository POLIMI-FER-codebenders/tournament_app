import "../styles/TournamentListComponent.css";
import React from 'react';
import { TournamentEntry } from "./TournamentEntry";
class ListTournamentComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      currentview: null
    };
    this.refreshView = this.refreshView.bind(this);

  }
  refreshView(viewindex) {
    this.setState({ currentview: viewindex })
  }

  render() {
    return (
      <div class="list">
        <div class="list-headers flex-container">
          <div class="col1 flex-items">Name</div>
          <div class="col2 flex-items">Starting Date</div>
          <div class="col3 flex-items">Type</div>
          <div class="col4 flex-items"></div>
          <div class="col5 flex-items"></div>
        </div>
        {this.props.tournaments.map((object, i) => <TournamentEntry record={object} key={i} viewindex={i} refreshView={this.refreshView} currentview={this.state.currentview} />)}
      </div>
    );

  }
}

export default ListTournamentComponent;