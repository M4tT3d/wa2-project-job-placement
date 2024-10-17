import { ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart"
import { Label, PolarRadiusAxis, RadialBar, RadialBarChart } from "recharts"

export interface RadialChartParameter {
  title: string
  description?: string
  data: any
  colorFill: string[]
  chartConfig: any
  cardFooter?: string
}

interface RenderRadialChartProps {
  parameter: RadialChartParameter
  className?: string
}

const RenderRadialChart: React.FC<RenderRadialChartProps> = ({ parameter }) => {
  const total = parameter.data.reduce((sum: number, item: Object) => {
    return sum + Object.values(item).reduce((acc, value) => acc + value, 0)
  }, 0)
  return (
    <ChartContainer
      config={parameter.chartConfig}
      className="mx-auto aspect-square w-full max-w-[250px]"
    >
      <RadialBarChart data={parameter.data} endAngle={180} innerRadius={80} outerRadius={130}>
        <ChartTooltip
          cursor={false}
          content={<ChartTooltipContent hideLabel />}
          wrapperStyle={{ backgroundColor: "#FFFFF0" }}
        />
        <PolarRadiusAxis tick={false} tickLine={false} axisLine={false}>
          <Label
            content={({ viewBox }) => {
              if (viewBox && "cx" in viewBox && "cy" in viewBox) {
                return (
                  <text x={viewBox.cx} y={viewBox.cy} textAnchor="middle">
                    <tspan
                      x={viewBox.cx}
                      y={(viewBox.cy || 0) - 16}
                      className="fill-foreground text-2xl font-bold"
                    >
                      {total}
                    </tspan>
                    <tspan
                      x={viewBox.cx}
                      y={(viewBox.cy || 0) + 4}
                      className="fill-muted-foreground"
                    >
                      {parameter.title}
                    </tspan>
                  </text>
                )
              }
            }}
          />
        </PolarRadiusAxis>
        {Object.keys(parameter.data[0]).map((key, index) => (
          <RadialBar
            dataKey={key}
            stackId="a"
            cornerRadius={5}
            fill={parameter.colorFill[index]}
            className="stroke-transparent stroke-2"
          />
        ))}
      </RadialBarChart>
    </ChartContainer>
  )
}

export default RenderRadialChart
