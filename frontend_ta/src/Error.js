import { useRouteError } from "react-router-dom";
import {useLocation} from 'react-router-dom';

export default function ErrorPage(props) {
  let defaultmessage=null;
  let messagetoshow=null;
  const location = useLocation();
  if(location.state==null) {
    if(window.location.href=="http://localhost:3000/error") defaultmessage = <p>Error Page</p>
    else defaultmessage=<p>Wrong url</p>
  } 
  else if(location.state.message!=null) messagetoshow=location.state.message;
  
   return (
    <div id="error-page">
      <h1>Oops!</h1>
      <p>Sorry, an unexpected error has occurred.</p>
       {defaultmessage}
      <p>{messagetoshow}</p>
    </div>
  );
}