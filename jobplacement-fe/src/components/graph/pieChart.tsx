import {Label, Pie, PieChart} from "recharts"

import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle,} from "@/components/ui/card"
import {ChartContainer, ChartTooltip, ChartTooltipContent,} from "@/components/ui/chart"

export class PieChartParameter {
    title: string;
    description: string;
    data: any;
    dataKey: string;
    nameKey: string;
    chartConfig: any;
    cardFooter: string;
    width: string;
    height: string;

    constructor(title: string, description: string, data: any, dataKey: string, nameKey: string, chartConfig: any, cardFooter: string, width: string, height: string) {
        this.title = title;
        this.description = description;
        this.data = data;
        this.dataKey = dataKey;
        this.nameKey = nameKey;
        this.chartConfig = chartConfig;
        this.cardFooter = cardFooter;
        this.width = width;
        this.height = height;
    }

}


export function renderPieChart(parameter: PieChartParameter) {
    /*const totalVisitors = React.useMemo(() => {
        return chartData.reduce((acc, curr) => acc + curr.visitors, 0)
    }, [])*/
    const totalVisitors = parameter.data.reduce((acc: any, curr: { value: any }) => acc + curr.value, 0)

    return (
        <Card className="flex flex-col" style={{width: "400px", height: "300px"}}>
            <CardHeader className="items-center pb-0">
                <CardTitle>{parameter.title}</CardTitle>
                <CardDescription>{parameter.description}</CardDescription>
            </CardHeader>
            <CardContent className="flex-1 pb-0">
                <ChartContainer
                    config={parameter.chartConfig}
                    className="mx-auto aspect-square max-h-[250px]"
                >
                    <PieChart>
                        <ChartTooltip
                            cursor={false}
                            content={<ChartTooltipContent hideLabel/>}
                        />
                        <Pie
                            data={parameter.data}
                            dataKey={parameter.dataKey}
                            nameKey={parameter.nameKey}
                            innerRadius={60}
                            strokeWidth={5}
                        >
                            <Label
                                content={({viewBox}) => {
                                    if (viewBox && "cx" in viewBox && "cy" in viewBox) {
                                        return (
                                            <text
                                                x={viewBox.cx}
                                                y={viewBox.cy}
                                                textAnchor="middle"
                                                dominantBaseline="middle"
                                            >
                                                <tspan
                                                    x={viewBox.cx}
                                                    y={viewBox.cy}
                                                    className="fill-foreground text-3xl font-bold"
                                                >
                                                    {totalVisitors.toLocaleString()}
                                                </tspan>
                                                <tspan
                                                    x={viewBox.cx}
                                                    y={(viewBox.cy || 0) + 24}
                                                    className="fill-muted-foreground"
                                                >
                                                    Visitors
                                                </tspan>
                                            </text>
                                        )
                                    }
                                }}
                            />
                        </Pie>
                    </PieChart>
                </ChartContainer>
            </CardContent>
            <CardFooter className="flex-col gap-2 text-sm">
                <div className="flex items-center gap-2 font-medium leading-none">
                    {parameter.cardFooter}
                </div>
            </CardFooter>
        </Card>
    )
}
