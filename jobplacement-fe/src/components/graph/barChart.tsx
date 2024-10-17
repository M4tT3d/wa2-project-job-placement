import {ChartContainer, ChartTooltip, ChartTooltipContent} from "@/components/ui/chart.tsx";
import {Bar, BarChart, CartesianGrid, XAxis, YAxis} from "recharts";
import React from 'react';

export interface BarChartParameter {
    title: string;
    description?: string;
    data: any;
    dataKey: string;
    valueKey: string[];
    colorFill: string[];
    chartConfig: any;
    cardFooter?: string;
    sliceAxis: number
}

interface RenderBarChartProps {
    parameter: BarChartParameter;
    orientation: "vertical" | "horizontal";
    className?: string;
}

const RenderBarChart: React.FC<RenderBarChartProps> = ({parameter, orientation}) => {
    const isVertical = orientation === 'vertical';
    return (
        <ChartContainer config={parameter.chartConfig}>
            {isVertical ? vertical(parameter) : horizontal(parameter)}
        </ChartContainer>
    );
}

export default RenderBarChart;

function vertical(parameter: BarChartParameter) {
    return (
        <BarChart accessibilityLayer data={parameter.data}>
            <CartesianGrid vertical={false}/>
            <XAxis
                dataKey={parameter.dataKey}
                tickLine={true}
                tickMargin={10}
                axisLine={false}
                tickFormatter={(value) => value.slice(0, parameter.sliceAxis)}
            />
            <ChartTooltip
                cursor={false}
                content={<ChartTooltipContent indicator="dashed"/>}
                wrapperStyle={{backgroundColor: "#FFFFF0"}}
            />
            {parameter.valueKey.map((_, index) => (
                <Bar dataKey={parameter.valueKey[index]} fill={parameter.colorFill[index]} radius={4}/>
            ))}
        </BarChart>)
}

function horizontal(parameter: BarChartParameter) {
    return (
        <BarChart
            accessibilityLayer
            data={parameter.data}
            layout="vertical"
            margin={{
                left: 30
            }}
        >
            <YAxis
                dataKey={parameter.dataKey}
                type="category"
                tickLine={false}
                tickMargin={10}
                axisLine={false}
                tickFormatter={(value) => value.slice(0, parameter.sliceAxis)}
            />
            <XAxis
                dataKey={parameter.data.some((item: {
                    jobOffer: number;
                    professional: number
                }) => item['jobOffer'] > item['professional']) ? 'jobOffer' : 'professional'}
                type="number" hide/>
            <ChartTooltip
                cursor={false}
                content={<ChartTooltipContent indicator="dashed"/>}
                wrapperStyle={{backgroundColor: "#FFFFF0"}}
            />
            {parameter.valueKey.map((key, index) => (
                <Bar key={index} dataKey={key} layout="vertical" fill={parameter.colorFill[index]} radius={5}/>
            ))}
        </BarChart>
    )
}