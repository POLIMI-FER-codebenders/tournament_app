import { Component } from "react";
import "../styles/ConfDialogue.css";
export class ConfDialogue extends Component{
    constructor(props) {
        super(props);
        this.state = {
            
        };
        this.Accept=this.Accept.bind(this);
        this.Refuse=this.Refuse.bind(this);
      }
Accept(){
    this.props.accept();
   
}
Refuse(){
this.props.refuse();

}
render(){
    
    return(<div id="confirmationid">
        {this.props.text}
        <div id="confbuttonstm">
        <button id="yesbuttonctm" onClick={this.Accept}>Yes</button>
        <button id="nobuttonctm" onClick={
            this.Refuse
            }>No</button>
        </div> 
         
    </div>)
}
}