import { useEffect, useState } from "react";
import { PageTitle, ScheduleModal } from "../../components";
import monitorsGroupService from "../../services/monitorsGroupService";
import { AnimatePresence, motion } from "framer-motion"
import { ALL_WEEKDAYS } from 'rrule'

function Schedule(){
    const [groups, setGroups] = useState([]);
    const [selectedGroupId, setSelectedGroupId] = useState(null);
    const [showPortal, setShowPortal] = useState(false);
    const [showGroupNeeded, setShowGroupNeeded] = useState(false);
    const [updater, setUpdater] = useState(false);
    const [rules, setRules] = useState([]);

    useEffect(() => {
        monitorsGroupService.getGroups().then((response) => {
            setGroups(response.data);
        })
    }, []);

    useEffect(() => {
        if (selectedGroupId !== null){
            monitorsGroupService.getGroup(selectedGroupId + 1).then((response) => {
                setRules(response.data.templateGroups);
            })
        }
    }, [selectedGroupId, updater]);
    
    const displayRules = () => {
        if (selectedGroupId === null){
            return(
                <div className="flex pt-[15%] place-content-center w-full h-full">
                    <span className="pt-[10%] text-xl font-light">Select a group to see its rules</span>
                </div>
            );
        }
        else {
            if (rules.length === 0){
                return(
                    <div className="flex pt-[15%] place-content-center w-full h-full">
                        <span className="pt-[10%] text-xl font-light">This group contains no rules</span>
                    </div>
                );
            }
            else {
                return(
                    <>
                        {rules.map((rule) => (
                            <div key={rule.id} className="flex w-[90%] p-2">
                                <span className="w-full h-full bg-secondaryLight rounded-md p-2">
                                    {rule.template.name} running {/* */}
                                    weekly from {/* */}
                                    {rule.schedule.startTime[0]}:{rule.schedule.startTime[1]} - {rule.schedule.endTime[0]}:{rule.schedule.endTime[1]} on {/* */}
                                    {rule.schedule.weekdays.map((day) => (
                                        <>{ALL_WEEKDAYS[day]}{/* */} </>
                                    ))}
                                </span> 
                            </div>
                        ))}
                    </>
                );
            }
        }
    }


    return(
        <div className="h-full flex flex-col">
            <div id="title" className="pt-4 h-[8%]">
                <PageTitle startTitle={"schedule"} 
                            middleTitle={"dashboard"}
                            endTitle={"dashboard"}/>
            </div>
            <div id="divider" className="flex h-[92%] mr-3 ml-3 ">
                <div className="flex flex-col w-[25%] h-full pt-4">
                    <div className="flex flex-row w-full h-[5%]">
                        <div className="flex w-[50%] h-full items-center">
                            <motion.button 
                                whileHover={{ 
                                    scale: 1.1, 
                                    border: "2px solid", 
                                    transition: {
                                        duration: 0.2,
                                        ease: "easeInOut",
                                    }, 
                                }}
                                whileTap={{ scale: 0.9 }}
                                onClick={() => { selectedGroupId === null ? setShowGroupNeeded(true) : setShowPortal(true) }}
                                className="bg-secondaryLight rounded-md h-[80%] pr-4 pl-4">
                                + Add rule
                            </motion.button>
                        </div>
                        
                        <div className="flex w-[50%] h-full items-center relative">
                            <motion.select
                                whileHover={{ border: "2px solid" }}
                                whileTap={{ border: "2px solid" }}
                                onChange={(e) => {setSelectedGroupId(e.target.value - 1); setShowGroupNeeded(false)}} 
                                className="ml-auto mr-5 bg-secondaryLight rounded-md h-[80%] pr-3 pl-3 cursor-pointer">
                                <option selected disabled hidden>Group</option>
                                {groups.length !== 0 && groups.map((group) => 
                                    <option value={group.id}>{group.name}</option>
                                )}
                            </motion.select>
                            {showGroupNeeded && 
                                <div className="absolute text-md text-red h-full top-10 right-1">
                                    You must select a group
                                </div>
                            }
                        </div>
                    </div>
                    <AnimatePresence>
                        {showPortal && <ScheduleModal
                                setShowPortal={setShowPortal}
                                selectedGroup={groups.at(selectedGroupId)}
                                updater={updater}
                                setUpdater={setUpdater} />
                        }
                    </AnimatePresence>
                    <div className="w-full h-[95%] pt-3">
                        {displayRules()}
                    </div>
                </div>
                <div id="dividerHr" className="mt-1 mb-1 w-[1px] h-full border-[1px] border-secondary"/>
                <div className="w-[74%] bg-secondaryLight rounded-md ml-3 mt-3">

                </div>
            </div>
        </div>
    )
}

export default Schedule;