type LabelType = {
    [key: string]: { label: string }
};

export const Legend = ({label, colorFill}: {
    label: LabelType
    colorFill: string[],
}) => {
    
    return (
        <div style={{display: 'flex', flexWrap: 'wrap'}}>
            {Object.entries(label).map(([, value], index) => (
                <div key={index}
                     style={{display: 'flex', alignItems: 'center', marginBottom: '8px', marginRight: '8px'}}>
                    <div style={{
                        width: '12px',
                        height: '12px',
                        backgroundColor: colorFill[index],
                        marginRight: '8px'
                    }}></div>
                    <span>{value.label}</span>
                </div>
            ))}
        </div>
    );
};