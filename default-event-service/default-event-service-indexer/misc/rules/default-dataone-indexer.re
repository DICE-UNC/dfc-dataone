
 acPostProcForOpen {
   *err = errormsg(ipc_acPostProcForOpen, *msg);
   if (*err < 0) { writeLine('serverLog', *msg); }
 }
 
 
 sendDataObjectOpen(*Data) =
   let *msg = ipc_jsonDocument(list(mkEntityField(*Data),
                                    mkPathField($objPath),
                                    mkUserObject('author', $userNameClient, $rodsZoneClient)))
   in sendMsg(DATA_OBJECT_TYPE ++ '.open', *msg)
 