import { createPortal } from 'react-dom';
import { MdArrowBack, MdCheck } from "react-icons/md";
import DataTable from 'react-data-table-component';
import { useState } from 'react';
import monitorService from '../../services/monitorService';
import PropTypes from 'prop-types';
import { motion } from 'framer-motion';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';


function PendingMonitorsModal( { showPortal, setShowPortal, monitorListUpdate, setMonitorListUpdate } ) {
    const queryClient = useQueryClient();

    const [disabled, setDisabled] = useState(false);
    const [updatePage, setUpdatePage] = useState(false);

    const pendingMonitorsQuery = useQuery({
        queryKey: ['pendingMonitorsQuery'],
        queryFn: () => monitorService.getPendingMonitors()
    })

    const acceptMonitorMutate = useMutation({
        mutationFn: (id) => monitorService.acceptMonitor(id),
        onSuccess: (data, id) => {
            pendingMonitorsQuery.data.data = pendingMonitorsQuery.data.data.filter((element) => element.id !== id)
            setUpdatePage(!updatePage);
        }
    })

    const { mutate: acceptMonitor } = acceptMonitorMutate;

    const columns = [
        {
            name: 'Name',
            selector: row => row.name,
        },
        {
            name: 'accept',
            cell: (row) => <button className="bg-[#97D700] size-8 mr-3 rounded-sm flex items-center text-lg" onClick={()=>acceptMonitor(row.id)}><MdCheck className='mx-auto'/></button>
        },
        {
            name: 'decline',
            cell: (row) => <button className="bg-[#D12E2E] size-8 rounded-sm flex items-center text-lg"><span className='mx-auto'>X</span></button>
        },
    ];

    const handleRefresh = () => {
        queryClient.invalidateQueries({queryKey: ['pendingMonitorsQuery']});
        setDisabled(true);
    
        setTimeout(() => {
          setDisabled(false);
        }, 3000);
    };

    return (
    <>
        {showPortal && createPortal(
            <div className="fixed z-10 top-0 h-screen w-screen backdrop-blur-sm flex">
                <div className="bg-black h-screen w-screen opacity-75"></div>
                <div className="absolute text-gray-50 h-screen w-screen flex items-center">
                    <div className="bg-[#fafdf7] text-[#101604] h-[75%] w-[70%] mx-auto rounded-xl p-[2%]">
                        <div className="h-[5%] flex items-center">
                            <button onClick={() => {setShowPortal(false); setMonitorListUpdate(!monitorListUpdate)}} className="flex flex-row">
                                <MdArrowBack className="w-7 h-7 mr-2"/> 
                                <span className="text-xl">Go back</span>
                            </button>
                        </div>
                        <div className="h-[90%] p-[4%]">
                            <div className="flex h-[20%] font-bold text-3xl">
                                <div className="w-[70%] h-full">
                                    <span>Pending Monitors</span>
                                </div>
                                <div className="w-[30%] h-full text-lg font-normal relative text-end">
                                    <motion.button
                                        whileTap={disabled ? {} : { scale: 0.9 }}
                                        whileHover={disabled ? {} : { scale: 1.1 }}
                                        onClick={handleRefresh} 
                                        disabled={disabled}
                                        className={`bg-[#d7dad6] p-2 pr-4 pl-4 rounded-md ${disabled ? "cursor-not-allowed opacity-40" : "cursor-pointer opacity-100"}`}>
                                        Refresh
                                    </motion.button>
                                </div>
                            </div>
                            <div className="h-[80%] p-[2%] text-lg flex flex-col">
                            <DataTable className="p-3" 
                                noTableHead
                                theme="solarized"
                                columns={columns}
                                progressPending={pendingMonitorsQuery.isLoading}
                                data={pendingMonitorsQuery.data?.data}
                            />
                            </div>
                        </div>
                    </div>
                </div>
            </div>,
        document.body
        )}
    </>
    )
}


PendingMonitorsModal.propTypes = {
    showPortal: PropTypes.bool.isRequired,
    setShowPortal: PropTypes.func.isRequired,
    monitorListUpdate: PropTypes.func.isRequired,
    setMonitorListUpdate: PropTypes.func.isRequired
}

export default PendingMonitorsModal;