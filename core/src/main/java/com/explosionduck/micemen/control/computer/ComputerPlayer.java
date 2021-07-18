package com.explosionduck.micemen.control.computer;

import com.explosionduck.micemen.control.Controller;

public interface ComputerPlayer extends Controller {

    ComputerPlayerType getComputerPlayerType();

    int getLookAhead();
}
