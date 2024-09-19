import { createPortal } from 'react-dom';
import { MdArrowBack, MdArrowDropUp } from "react-icons/md";
import PropTypes from 'prop-types';
import { useEffect, useState } from 'react';
import templateService from "../../../services/templateService";
import mediaService from '../../../services/mediaService';
import ScheduleContentModal from './ScheduleContentModal';

import DatePicker from 'react-date-picker';
import 'react-date-picker/dist/DatePicker.css';
import 'react-calendar/dist/Calendar.css';
import { AnimatePresence, motion } from 'framer-motion';
import { colors, weekDays, timeHour, timeMinute, getWeekDaysValues } from './scheduleModalUtils';
import Widgets from '../../Widgets';
import { useUserStore } from '../../../stores/useUserStore';
import ruleService from '../../../services/ruleService';
import { useMutation, useQueries } from '@tanstack/react-query';


function ScheduleModal( { setShowPortal, selectedGroup, updater, setUpdater, totalRules, titleMessage, ruleId, setRuleId, edit, setEdit } ) {
    const [selectedColors,setSelectedColors] = useState([]);

    const [selectedButtonTemplateIndex, setSelectedButtonTemplateIndex] = useState(null);
    const [selectedTemplateId, setSelectedTemplateId] = useState(null);
    const [selectedDays, setSelectedDays] = useState([]);
    const [selectedStartTime, setSelectedStartTime] = useState([null, null]);
    const [selectedEndTime, setSelectedEndTime] = useState([null, null]);
    const [selectedStartDate, setSelectedStartDate] = useState(null);
    const [selectedEndDate, setSelectedEndDate] = useState(null);
    const [selectedContent, setSelectedContent] = useState({});
    const [priority,setPriority] = useState(null);

    const [showContentsPortal, setShowContentsPortal] = useState(false);
    const [selectedWidgetId, setSelectedWidgetId] = useState(null);

    const [displayInfo, setDisplayInfo] = useState(false);
    const username = useUserStore((state) => state.username);


    useEffect(()=>{
        if (!templatesQuery.isLoading && templatesQuery.data.data.length !== 0 && selectedTemplateId !== null){
            let template = templatesQuery.data.data.at(selectedButtonTemplateIndex).widgets.length;
            let arr = [];
            for (let i = 0; i < template; i++){
                arr.push(colors[Math.floor(Math.random() * colors.length)]);
            }
            setSelectedColors(arr)
        }
    },[selectedTemplateId]);

    const [templatesQuery] = useQueries({
        queries : [
            {
                queryKey: ['templates'],
                queryFn: () => templateService.getTemplates().then((response) => {
                    if (ruleId !== null) {
                        getRuleInfo(response.data);
                    }
                    return response;
                })
            }
        ]
    });

    const getRuleInfo = (templates) => {
        ruleService.getRuleById(ruleId).then((response) => {
            const data = response.data;
            const schedule = data.schedule;
            const content = data.chosenValues;
            
            setPriority(schedule.priority)
            setSelectedTemplateId(data.template.id);
            setSelectedDays(schedule.weekdays);
            setSelectedStartTime([schedule.startTime[0].toString().padStart(2, '0'), schedule.startTime[1].toString().padStart(2, '0')]);
            setSelectedEndTime([schedule.endTime[0].toString().padStart(2, '0'), schedule.endTime[1].toString().padStart(2, '0')]);


            // load existing content to widgets
            const tmpSelectedContent = { ...selectedContent };

            Object.keys(content).forEach((widgetId) => {
                if (!tmpSelectedContent[widgetId]) {
                    tmpSelectedContent[widgetId] = {};
                }
                Object.keys(content[widgetId]).forEach((variable) => {
                    // If variable is media type
                    if (data.template.widgets.find(x => x.id == widgetId).widget.variables.find(x => x.name == variable).type === 'MEDIA') {
                        mediaService.getFileOrDirectoryById(content[widgetId][variable]).then((response) => {
                            tmpSelectedContent[widgetId][variable] = response.data;
                        });
                    } else {
                        tmpSelectedContent[widgetId][variable] = content[widgetId][variable];
                    }
                });
            });

            setSelectedContent(tmpSelectedContent);

            if (schedule.startDate) setSelectedStartDate(new Date(schedule.startDate));
            if (schedule.endDate) setSelectedEndDate(new Date(schedule.endDate));

            setSelectedButtonTemplateIndex(templates?.findIndex((x) => x.id === data.template.id));
        })
    }

    const addRuleMutation = useMutation({
        mutationFn: (data) => ruleService.addRule(data),
        onSuccess: () => {
            setUpdater(!updater);
        }
    });

    const updateRuleMutation = useMutation({
        mutationFn: (data) => ruleService.updateRule(ruleId, data),
        onSuccess: () => {
            setUpdater(!updater);
            setEdit(false);
        }
    });

    const { mutate: addRuleMutate } = addRuleMutation;
    const { mutate: updateRuleMutate } = updateRuleMutation;

    const handleSubmit = () => {
        const data = {
            "groupId": selectedGroup.id,
            "templateId": selectedTemplateId,
            "schedule": { 
                "startTime": selectedStartTime.join(':'),
                "endTime": selectedEndTime.join(':'),
                "startDate": selectedStartDate,
                "endDate": selectedEndDate,
                "priority": edit ? priority : totalRules,
                "weekdays": selectedDays,
                "createdBy": username,
                "lastEditedBy": username,
                "createdOn": new Date() 
            },
            "chosenValues": selectedContent
        }

        if (edit) {
            updateRuleMutate(data);
        } else {
            addRuleMutate(data);
        }

        resetEverything();
        setShowPortal(false);
    };

    const handleSelectedDays = (event) => {
        const value = parseInt(event.target.value);

        if (selectedDays.includes(value)) {
            setSelectedDays(selectedDays.filter(day => day !== value));
        } 
        else {
            setSelectedDays([...selectedDays, value]);
        }
    };


    const handleDisplayAllTime = () => {
        setSelectedStartTime(["00", "00"]);
        setSelectedEndTime(["23", "55"]);
        setSelectedDays(getWeekDaysValues());
    };


    const handleDisplayWeeklyFrom8Till23 = () => {
        setSelectedStartTime(["08", "40"]);
        setSelectedEndTime(["23", "00"]);
        setSelectedDays(getWeekDaysValues());
    };

    const contentElement = (templateWidget) => {
        const widgetVariables = templateWidget.widget.variables;

        if (widgetVariables.length === 0){
            return ( <Widgets widgetType={"default"} templateWidget={templateWidget}/> );
        }
        else {
            return ( <Widgets widgetType={templateWidget.widget.variables[0].type} 
                            templateWidget={templateWidget} 
                            setShowContentsPortal={setShowContentsPortal}
                            setSelectedWidgetId={setSelectedWidgetId}
                            selectedContent={selectedContent}/> );
        }
    }

    const resetEverything = () => {
        setRuleId(null);
        setEdit(false);
        setShowPortal(false); 
    }

    const goBackButton = () => {
        return (
            <button onClick={() => { resetEverything() }} className="flex flex-row">
                <MdArrowBack className="w-7 h-7 mr-2"/> 
                <span className="text-xl">Go back</span>
            </button>
        )
    }

    const selectTemplateButton = () => {
        return (
            <select id="templateSelect" 
                    value={selectedTemplateId || ""}
                    onChange={(e) => {setSelectedContent({}); 
                                        setSelectedButtonTemplateIndex(JSON.parse(e.target.value).values[0]); 
                                        setSelectedTemplateId(JSON.parse(e.target.value).values[1])}} 
                    className="bg-[#E9E9E9] rounded-md p-2">
                <option value="" disabled hidden>Template</option>
                {templatesQuery.data.data.length !== 0 && templatesQuery.data.data.map((template, index) => 
                    <option key={template.id} value={JSON.stringify({ values: [index, template.id] })}>{template.name}</option>
                )}
            </select>
        )
    }

    const selectRulesButton = () => {
        return (
            <select defaultValue="" className="bg-[#E9E9E9] rounded-md p-2 cursor-pointer">
                <option value="" disabled hidden>Default rules</option>
                <option onClick={handleDisplayAllTime}>Display 24/7</option>
                <option onClick={handleDisplayWeeklyFrom8Till23}>Weekly 08:40 - 23:00</option>
            </select>
        )
    }

    const startTimeDisplay = () => {
        return (
            <div className="h-full w-[50%] flex flex-col items-center justify-center">
                <span className="pb-1">Start Time:</span>
                <div className="flex items-center rounded-md border border-gray-300">
                    <select value={selectedStartTime[0] || ""} 
                            onChange={(event) => setSelectedStartTime([event.target.value, selectedStartTime[1]])} 
                            className="p-2 pr-0 appearance-none bg-transparent border-none outline-none">
                        <option value="" disabled hidden>--</option>
                        {timeHour
                            .filter(hour => selectedEndTime[0] === null || hour < selectedEndTime[0])
                            .map((hour) => (
                                <option key={hour} value={hour}>
                                    {hour}
                                </option>
                        ))}
                    </select>
                    <span className="mx-2">:</span>
                    <select value={selectedStartTime[1] || ""} 
                            onChange={(event) => setSelectedStartTime([selectedStartTime[0], event.target.value])} 
                            className="p-2 pl-0 appearance-none bg-transparent border-none outline-none">
                        <option value="" disabled hidden>--</option>
                        {timeMinute.map((minute) => (
                            <option key={minute} value={minute}>
                                {minute}
                            </option>
                        ))}
                    </select>
                </div>
            </div>
        )
    }

    const endTimeDisplay = () => {
        return (
            <div className="h-full w-[50%] flex flex-col items-center justify-center">
                <span className="pb-1">End Time:</span>
                <div className="flex items-center rounded-md border border-gray-300">
                    <select value={selectedEndTime[0] || ""} 
                            onChange={(event) => setSelectedEndTime([event.target.value, selectedEndTime[1]])} 
                            className="p-2 pr-0 appearance-none bg-transparent border-none outline-none">
                        <option value="" disabled hidden>--</option>
                        {timeHour
                            .filter(hour => selectedStartTime[0] === null || hour > selectedStartTime[0])
                            .map((hour) => (
                                <option key={hour} value={hour}>
                                    {hour}
                                </option>
                        ))}
                    </select>
                    <span className="mx-2">:</span>
                    <select value={selectedEndTime[1] || ""} 
                            onChange={(event) => setSelectedEndTime([selectedEndTime[0], event.target.value])} 
                            className="p-2 pl-0 appearance-none bg-transparent border-none outline-none">
                        <option value="" disabled hidden>--</option>
                        {timeMinute.map((minute) => (
                            <option key={minute} value={minute}>
                                {minute}
                            </option>
                        ))}
                    </select>
                </div>
            </div>
        )
    }
    
    const weekdaysDisplay = () => {
        return (
            <div className="h-[50%] w-full p-3 pr-[10%] pl-[10%] place-content-center">
                <span>Weekdays:</span>
                <div className="flex justify-around pt-3 text-sm">
                    {weekDays.map((day) => (
                        <div key={day.value} className="flex flex-col items-center">
                            <input
                                onChange={handleSelectedDays}
                                checked={selectedDays.includes(day.value)}
                                value={day.value}
                                type="checkbox"
                                className="h-5 w-5"
                            />
                            <span>{day.label}</span>
                        </div>
                    ))}
                </div>
            </div>
        );
    }

    const startDateDisplay = () => {
        return (
            <div className="h-full w-[50%] flex flex-col items-center place-content-center">
                <span>Start date:</span>
                <DatePicker 
                    onChange={(date) => setSelectedStartDate(date)} 
                    value={selectedStartDate} 
                    minDate={new Date()}
                    format="dd/MM/y"/>
            </div>
        )
    }

    const endDateDisplay = () => {
        return (
            <div className="h-full w-[50%] flex flex-col items-center place-content-center">
                <span>End date:</span>
                <DatePicker 
                    onChange={(date) => setSelectedEndDate(date)} 
                    value={selectedEndDate} 
                    minDate={selectedStartDate !== null ? selectedStartDate : new Date()}
                    format="dd/MM/y"/>
            </div>
        )
    }

    const submitButton = () => {
        if (selectedTemplateId !== null && selectedDays.length !== 0 && !selectedStartTime.includes(null) && !selectedEndTime.includes(null)){
            return (
                <button onClick={handleSubmit} 
                    className="bg-[#96d600] rounded-md p-2 pl-4 pr-4">
                    {edit ? "Update rule" : "Create rule"}
                </button>
            )
        }
        else{
            return (
                <button
                    onMouseEnter={() => setDisplayInfo(true)}
                    onMouseLeave={() => setDisplayInfo(false)}
                    className='relative'>
                    <button onClick={handleSubmit} 
                        disabled 
                        className="bg-[#96d600] opacity-50 cursor-not-allowed rounded-md p-2 pl-4 pr-4">
                        {edit ? "Update rule" : "Create rule"}
                    </button>
                    {displayInfo &&
                        <>
                            <motion.div className="absolute min-w-64 max-w-64 bg-black text-white text-sm rounded py-1 px-3 left-[-50%] top-[110%]">
                                <div className="flex flex-col">
                                    <span>
                                        {selectedTemplateId === null ? "Missing template" : ""}
                                    </span>
                                    <span>
                                        {selectedDays.length === 0 ? "Missing days" : ""}
                                    </span>
                                    <span>
                                        {selectedStartTime.includes(null) ? "Missing start time" : ""}
                                    </span>
                                    <span>
                                        {selectedEndTime.includes(null) ? "Missing end time" : ""}
                                    </span>
                                </div>
                            </motion.div>
                            <MdArrowDropUp className="absolute top-[85%] left-[50%] translate-x-[-50%]"/>
                        </>
                    }
                </button>
            )
        }
    }

    const templatePreview = () => {
        if (selectedTemplateId !== null && templatesQuery.data.data.length !== 0){
            return (
                <>
                    {templatesQuery.data.data[selectedButtonTemplateIndex].widgets.map((templateWidget, index) => 
                        <div key={templateWidget.id} className={`absolute`}
                            style={{
                                width: `${templateWidget.width}%`,
                                height: `${templateWidget.height}%`,
                                top: `${templateWidget.top}%`,
                                left: `${templateWidget.left}%`,
                                zIndex: templateWidget.zindex,
                            }}> 
                            <div className={`h-full w-full absolute flex flex-col items-center place-content-center border-2 rounded-sm ${selectedColors[index]}`}>
                                {contentElement(templateWidget)}
                            </div>
                        </div>
                    )}
                </>

            )
        }
    }

    if (!templatesQuery.isLoading) {
        return createPortal(
            <motion.div key="background"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                transition={{ duration:0.3 }}
                className="fixed z-20 top-0 h-screen w-screen backdrop-blur-sm flex">
                <div className="bg-black h-screen w-screen opacity-75"></div>
                <motion.div key="content"
                    initial={{ scale: 0.8 }}
                    animate={{ scale: 1 }}
                    exit={{ scale: 0.8 }}
                    transition={{ duration: 0.3, ease: "easeOut" }}
                    className="absolute text-gray-50 h-screen w-screen flex items-center">
                    <div className="bg-[#fafdf7] text-[#101604] h-[90%] w-[90%] mx-auto rounded-xl p-[1%]">
                        <div className="h-[5%] w-full flex items-center">
                            {goBackButton()}
                        </div>
                        <div className="h-[90%] w-full flex flex-row">
                            <div className="w-[40%] text-xl">
                                <div className="w-full h-full flex flex-col items-center content-center place-items-center place-content-center">
                                    <span className="text-2xl">{titleMessage}<span className="font-medium">{selectedGroup.name}</span></span>
                                    <div className="text-lg flex flex-row w-full justify-evenly">
                                        <div className="flex pt-5">
                                            {selectTemplateButton()}
                                        </div>
                                        <div className="flex pt-5">
                                            {selectRulesButton()}
                                        </div>
                                    </div>
                                    <div className="h-[55%] w-full pt-[7%] pr-[12%] pl-[12%]">
                                        <div className="h-full w-full bg-[#E9E9E9] rounded-md">
                                            <div className="h-[25%] pt-3 w-full flex flex-row">
                                                {startTimeDisplay()}
                                                {endTimeDisplay()}
                                            </div>
                                            <div className="h-[75%] w-full flex">
                                                <div className="h-full w-full p-3">
                                                    {weekdaysDisplay()}
                                                    <div className="h-[50%] w-full flex flex-row">
                                                        {startDateDisplay()}
                                                        {endDateDisplay()}
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="flex text-xl h-[10%] pt-[6%] w-full items-center place-content-center ">
                                        {submitButton()}
                                    </div>
                                </div>
                            </div>
                            <div className="w-[60%] text-lg flex flex-col m-auto">
                                <div className="flex w-full h-[8%] items-center place-items-center">
                                    <span className="font-medium text-3xl pb-1">Template Preview:</span>
                                </div>
                                <div className="aspect-video relative border-4 border-gray-300 rounded-md">
                                    {templatePreview()}
                                    <AnimatePresence>
                                        {showContentsPortal && <ScheduleContentModal
                                            setShowContentsPortal={setShowContentsPortal}
                                            templateWidget={templatesQuery.data.data[selectedButtonTemplateIndex].widgets.find(x => x.id == selectedWidgetId)}
                                            selectedContent={selectedContent}
                                            setSelectedContent={setSelectedContent} />
                                        }
                                    </AnimatePresence>
                                </div>
                            </div>
                        </div>
                    </div>
                </motion.div>
            </motion.div>,
        document.body
        );
    }
}


ScheduleModal.propTypes = {
    setShowPortal: PropTypes.func.isRequired
}

export default ScheduleModal;