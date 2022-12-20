import '../styles/Event.css';
import React from 'react';
export class EventEntry extends React.Component {
    constructor(props) {
        super(props);
        this.state = {

        };
    }
    render() {
        return (
            <> <div id="evententrydiv">
                <div id="typediv">{this.props.record.type}</div>
                
                </div>
            </>
            
        
        )
    }
}
