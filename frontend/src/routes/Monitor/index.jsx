import { useState } from "react";
import { Errors, PageTitle } from "../../components";
import { MdArrowBack, MdMonitor } from "react-icons/md";
import { useNavigate, useParams } from "react-router";
import monitorService from "../../services/monitorService";
import monitorsGroupService from "../../services/monitorsGroupService";
import { Link } from "react-router-dom";
import { templateBlock } from "./templateConfig";
import { memoryBlock } from "./memoryConfig";
import { statusBlock } from "./statusConfig";
import { updatedBlock } from "./updatedConfig";
import { errorBlock } from "./errorConfig";
import { lastUpdateBlock } from "./lastUpdateConfig";
import { groupBlock } from "./groupConfig";
import { previewBlock } from "./previewConfig";
import { ErrorCode } from "../../components/Errors/errorUtils";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { loadingBlock } from "./loadingConfig";
import { showUpdatingMonitor } from "./updatingMonitorConfig";


function Monitor(){
    const queryClient = useQueryClient()
    const navigate = useNavigate();
    const { id } = useParams();

    const [name,setName] = useState("");
    const [groupId,setGroupId] = useState(-1);
    const [updatingMonitor, setUpdatingMonitor] = useState(false);
    
    const monitorByIdQuery = useQuery({
        queryKey: ['monitorById', updatingMonitor],
        queryFn: async() => {
            return await monitorService.getMonitorById(id).then((response) => {
                setGroupId(response.data.group.id);
                setName(response.data.name);
                return response;
            })
        }
    });

    const monitorsQuery = useQuery({
        queryKey: ['monitors', {updatingMonitor: false}],
        queryFn: async() => {
            return await monitorsGroupService.getNonDefaultGroups().then((response) => {
                return response;
            })
        },
        enabled: !monitorByIdQuery.isPending
    });

    const updateMonitorMutate = useMutation({
        mutationFn: () => {
            let monitorsend = monitorByIdQuery.data.data;
            monitorsend.name = name;

            const group = monitorsQuery.data.data.find(element => element.id == groupId);
            monitorsend.group = group.defaultGroup ? { id: null } : { id: group.id };

            return monitorService.updateMonitor(id, monitorsend);
        },
        onSuccess: (data) => {
            queryClient.setQueryData(['monitorById'], data)
            setUpdatingMonitor(false);
        }
    });

    const { mutate: updateMonitor } = updateMonitorMutate;

    if(!monitorByIdQuery.isPending){
        if (monitorByIdQuery.data !== undefined){
            if(!monitorsQuery.isPending){
                return(
                    <div className="h-full flex flex-col">
                        <div id="title" className="pt-4 h-[8%]">
                            <PageTitle startTitle={"monitor"} 
                                        middleTitle={"default"}
                                        endTitle={"dashboard"}/>
                        </div>
                        <div id="monitor" className="h-[92%]">
                            <div className="h-[8%] w-full flex flex-row">
                                <button onClick={() => navigate("/monitors")} className="w-[50%]">
                                    <div className="w-[100%] flex text-2xl gap-2 pb-2 items-end"><MdArrowBack className=" size-8"/> Go Back</div>
                                </button>
                                <div className="w-[50%] flex text-2xl gap-2 pb-2 pl-4 items-end"><MdMonitor className=" size-8"/>Preview</div>
                            </div>
                            <div className="h-[92%] w-full flex flex-row gap-5">
                                <div className="w-[50%] h-full bg-secondaryLight rounded-[30px] flex flex-col p-5 gap-5">
                                    <div className=" text-center text-4xl flex flex-row items-center justify-center gap-4">
                                        {showUpdatingMonitor(updatingMonitor, name, setName, updateMonitor, setUpdatingMonitor, monitorByIdQuery)}
                                    </div>
                                    <div className=" bg-secondaryMedium rounded-[20px] h-[30%] text-center flex flex-col p-4 pb-6">
                                        {templateBlock()}
                                    </div>
                                    <div className="h-[60%] flex-col flex gap-3 text-center text-xl">
                                        <div className="flex w-full gap-3 h-[49%] justify-around">
                                            {memoryBlock()}
                                            {statusBlock(monitorByIdQuery.data.data.online)}
                                            {groupBlock(updatingMonitor, monitorByIdQuery.data.data, monitorsQuery.data.data, groupId, setGroupId)}
                                        </div>
                                        <div className="flex w-full gap-3 h-[49%] justify-around">
                                            {updatedBlock()}
                                            {errorBlock()}
                                            {lastUpdateBlock()}
                                        </div>
                                    </div>
                                </div>
                                <div className="w-[50%] h-full">
                                    <div className="h-[60%] bg-secondaryLight rounded-[30px] p-3 w-full">
                                        {previewBlock()}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                )
            }
        }
        else{
            return (
                <Errors errorCode={ErrorCode.PAGE_NOT_FOUND}/>
            )
        }
    }
    else{
        return(
            loadingBlock()
        )
        
    }
        
}

export default Monitor;