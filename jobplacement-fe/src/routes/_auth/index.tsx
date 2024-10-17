import {createFileRoute} from '@tanstack/react-router'
import {useEffect, useState} from 'react'
import RenderBarChart, {BarChartParameter,} from '@/components/graph/barChart.tsx'
import {COLORS, configJobOffer, configProfessionalRadial, configSkill,} from '@/constants/chartConfig.ts'
import RenderRadialChart, {RadialChartParameter,} from '@/components/graph/radialChart.tsx'
import {RenderCard} from '@/components/graph/RenderCard.tsx'
import {Legend} from '@/components/ui/legend.tsx'
import {AiOutlineFall, AiOutlineRise} from "react-icons/ai";

export const Route = createFileRoute('/_auth/')({
    component: showAnalytics,
})

function showAnalytics() {
    const [jobOffer, setJobOffer] = useState(null)
    const [jobOfferMaxMinAvaregaValue, setJobOfferMaxMinAvaregaValue] =
        useState(null)
    const [skill, setSkill] = useState(null)
    const [professional, setProfessional] = useState(null)
    const [numberCustomer, setNumberCustomer] = useState(null)
    const [skillMostLeastUsedJobOffer, setSkillMostLeastUsedJobOffer] =
        useState(null)
    const [skillMostLeastUsedProfessional, setSkillMostLeastUsedProfessional] =
        useState(null)
    const [error, setError] = useState(null)

    const [selectedCategory, setSelectedCategory] = useState<string>('Professional skills')

    const handleClick = (category: string) => {
        setSelectedCategory(category)
    }
    let skillMostLeastUsed: string[] = new Array(4)

    useEffect(() => {
        async function fetchData() {
            try {
                const jobOffer = await fetch(
                    'http://localhost:8080/analytics/api/job-offer-state',
                )

                const jobOfferMaxMinAvaregaValue = await fetch(
                    'http://localhost:8080/analytics/api/job-offer/max-min-average-value',
                )
                const skill = await fetch(
                    'http://localhost:8080/analytics/api/skill/all',
                )

                const skillMostLeastUsedJobOffer = await fetch(
                    'http://localhost:8080/analytics/api/skill/jo/mostAndLeast',
                )

                const skillMostLeastUsedProfessional = await fetch(
                    'http://localhost:8080/analytics/api/skill/p/mostAndLeast',
                )
                const professional = await fetch(
                    'http://localhost:8080/analytics/api/professional',
                )
                const numberCustomer = await fetch(
                    'http://localhost:8080/analytics/api/customer/number',
                )
                if (
                    !jobOffer.ok ||
                    !skill.ok ||
                    !professional.ok ||
                    !numberCustomer.ok ||
                    !skillMostLeastUsedJobOffer.ok ||
                    !skillMostLeastUsedProfessional.ok ||
                    !jobOfferMaxMinAvaregaValue.ok
                ) {
                    throw new Error(`HTTP error!`)
                }
                const jsonJobOffer = await jobOffer.json()
                const jsonJobOfferMaxMinAvaregaValue =
                    await jobOfferMaxMinAvaregaValue.json()
                const jsonSkill = await skill.json()
                const jsonProfessional = await professional.json()
                const jsonNumberCustomer = await numberCustomer.json()
                const jsonSkillMostLeastUsedJobOffer =
                    await skillMostLeastUsedJobOffer.json()
                const jsonSkillMostLeastUsedProfessional =
                    await skillMostLeastUsedProfessional.json()
                setJobOffer(jsonJobOffer)
                setJobOfferMaxMinAvaregaValue(jsonJobOfferMaxMinAvaregaValue)
                setSkill(jsonSkill)
                setProfessional(jsonProfessional)
                setNumberCustomer(jsonNumberCustomer)
                setSkillMostLeastUsedJobOffer(jsonSkillMostLeastUsedJobOffer)
                setSkillMostLeastUsedProfessional(jsonSkillMostLeastUsedProfessional)
            } catch (err: any) {
                setError(err.message)
            }
        }

        fetchData()
    }, [])
    if (error) {
        return (
            <div>
                <h2>Error fetching data</h2>
                <pre>{error}</pre>
            </div>
        )
    }
    if (
        jobOffer !== null &&
        skill !== null &&
        professional !== null &&
        numberCustomer !== null &&
        skillMostLeastUsedJobOffer !== null &&
        skillMostLeastUsedProfessional !== null &&
        jobOfferMaxMinAvaregaValue !== null
    ) {
        const chartProfessionalRadial: RadialChartParameter = {
            title: 'All our professionals',
            data: professionalFromBarToRadial(professional),
            colorFill: [COLORS.ORANGE, COLORS.BLUE, COLORS.YELLOW],
            chartConfig: configProfessionalRadial,
        }

        const chartJobOffer: BarChartParameter = {
            title: 'All our job offers',
            data: addColor(jobOffer, [
                COLORS.ORANGE,
                COLORS.RED,
                COLORS.YELLOW,
                COLORS.PURPLE,
                COLORS.SKY_BLUE,
                COLORS.GREEN,
            ]),
            dataKey: 'state',
            valueKey: ['value'],
            colorFill: [
                COLORS.ORANGE,
                COLORS.RED,
                COLORS.YELLOW,
                COLORS.PURPLE,
                COLORS.SKY_BLUE,
                COLORS.GREEN,
            ],
            chartConfig: configJobOffer,
            sliceAxis: 4,
        }

        const chartSkill: BarChartParameter = {
            title: 'All the skills that you can find',
            data: skill,
            dataKey: 'name',
            valueKey: ['jobOffer', 'professional'],
            colorFill: [COLORS.ORANGE, COLORS.BLUE],
            chartConfig: configSkill,
            sliceAxis: 15,
        }

        skillMostLeastUsed[0] = skillMostLeastUsedProfessional["mostRequest"]
        skillMostLeastUsed[1] = skillMostLeastUsedProfessional["leastRequest"]
        skillMostLeastUsed[2] = skillMostLeastUsedJobOffer["mostRequest"]
        skillMostLeastUsed[3] = skillMostLeastUsedJobOffer["leastRequest"]

        return (
            <div>
                <h1 className="text-5xl font-bold">
                    Job placement service
                </h1>
                <h2 className="text-3xl mt-4">
                    Welcome in our job placement service, the app that will make your life easier.
                </h2>
                <p className="text-2xl mt-4">
                    If you are interested in our services, you can look at our statistics and our achievements since the
                    beginning of our activity here.
                    If you are the manager of a company you can entrust the search for a specific professional to us, we
                    will find the person for you.
                </p>
                <div className="grid grid-rows-2 gap-4 grid-flow-col mt-8">
                    <RenderCard
                        title={chartJobOffer.title}
                        description={chartJobOffer.description}
                        className="row-span-2"
                        legend={
                            <div className="mt-2 text-center">
                                <Legend
                                    label={chartJobOffer.chartConfig}
                                    colorFill={chartJobOffer.colorFill}
                                />
                            </div>
                        }
                    >
                        <RenderBarChart
                            parameter={chartJobOffer}
                            orientation={'vertical'}
                        ></RenderBarChart>
                    </RenderCard>
                    <RenderCard
                        title={'All the customers who have relied on us'}
                        className="col-span-2 row-span-1"
                    >
                        <p className={'font-bold text-3xl text-purple-900 text-center'}>
                            {numberCustomer}
                        </p>
                    </RenderCard>
                    <RenderCard
                        title={chartProfessionalRadial.title}
                        description={chartProfessionalRadial.description}
                        className="row-span-1 col-span-2"
                        legend={
                            <div className="mt-2 text-center">
                                <Legend
                                    label={chartProfessionalRadial.chartConfig}
                                    colorFill={chartProfessionalRadial.colorFill}
                                />
                            </div>
                        }
                    >
                        <RenderRadialChart
                            parameter={chartProfessionalRadial}
                        ></RenderRadialChart>
                    </RenderCard>
                </div>
                <RenderCard
                    title={chartSkill.title}
                    description={chartSkill.description}
                    legend={
                        <div className="mt-2 text-center">
                            <Legend
                                label={chartSkill.chartConfig}
                                colorFill={chartSkill.colorFill}
                            />
                        </div>
                    }
                    className="my-4"
                >
                    <RenderBarChart parameter={chartSkill} orientation={'horizontal'}/>
                </RenderCard>

                <div className="grid grid-rows-1 gap-4 grid-flow-col">
                    <RenderCard title={"Our most and least in-need skills"} description={''}
                                className="row-span-2">
                        <ul className=" flex gap-2.5 mb-4 justify-center">
                            <li
                                onClick={() => handleClick('Professional skills')}
                                className={`cursor-pointer ${selectedCategory === 'Professional skills' ? 'text-primary border-b-4 border-primary' : 'text-gray-600'}`}
                            >
                                Professional skills
                            </li>
                            <li
                                onClick={() => handleClick('Job offer skills')}
                                className={`cursor-pointer ${selectedCategory === 'Job offer skills' ? 'text-primary border-b-4 border-primary' : 'text-gray-600'}`}
                            >
                                Job offer skills
                            </li>
                        </ul>
                        <div className="mt-2.5 text-2xl font-bold">
                            {selectedCategory === 'Professional skills' ? (
                                <>
                                    {skillMostLeastUsed[0] !== "none" ? (
                                        <>
                                            <div className="flex items-center mb-2">
                                                <AiOutlineRise className="mr-1.5 text-purple-900 text-3xl"/>
                                                <span
                                                    className="text-purple-900 text-3xl font-bold">{skillMostLeastUsed[0]}
                                                </span>
                                            </div>
                                            <div className="flex items-center">
                                                <AiOutlineFall className="mr-1.5 text-purple-900 text-3xl"/>
                                                <span className="text-purple-900 text-3xl font-bold">
                                                    {skillMostLeastUsed[1]}
                                                </span>
                                            </div>
                                        </>
                                    ) : (
                                        <>
                                            <div
                                                className="flex justify-center text-center text-purple-900 text-3xl font-bold">
                                                No data available
                                            </div>
                                        </>
                                    )}
                                </>) : (
                                <>
                                    {skillMostLeastUsed[2] !== "none" ? (
                                        <>
                                            <div className="flex items-center mb-2">
                                                <AiOutlineRise
                                                    className="mr-1.5 text-purple-900 text-3xl"/>
                                                <span
                                                    className="text-purple-900 text-3xl font-bold">{skillMostLeastUsed[2]}
                                                </span>
                                            </div>
                                            <div className="flex items-center">
                                                <AiOutlineFall
                                                    className="mr-1.5 text-purple-900 text-3xl"/>
                                                <span className="text-purple-900 text-3xl font-bold">
                                                    {skillMostLeastUsed[3]}
                                                </span>
                                            </div>
                                        </>
                                    ) : (
                                        <>
                                            <div
                                                className="flex justify-center text-center text-purple-900 text-3xl font-bold">
                                                No data available
                                            </div>
                                        </>
                                    )}
                                </>)
                            }
                        </div>
                    </RenderCard>
                    <RenderCard title={"Remuneration for job offers"}>
                        <div className="flex justify-between">
                            {jobOfferMaxMinAvaregaValue['maxRate'] !== "none" ? (
                                <>
                                    <span className="text-center">
                                        <p className="text-purple-900 text-3xl font-bold">
                                            {jobOfferMaxMinAvaregaValue['maxRate']}
                                        </p>
                                        <p className="text-gray-600">Max revenue</p>
                                    </span>
                                    <span className="text-center">
                                        <p className="text-purple-900 text-3xl font-bold">
                                            {jobOfferMaxMinAvaregaValue['minRate']}
                                        </p>
                                        <p className="text-gray-600">Min revenue</p>
                                    </span>
                                    <span className="text-center">
                                        <p className="text-purple-900 text-3xl font-bold">
                                            {jobOfferMaxMinAvaregaValue['averageRate']}
                                        </p>
                                    <p className="text-gray-600">Avg revenue</p>
                                    </span>
                                </>) : (
                                <div className="flex justify-center text-center text-purple-900 text-3xl font-bold">
                                    No data available
                                </div>
                            )}
                        </div>
                    </RenderCard>
                </div>
            </div>
        )
    }
}

export default showAnalytics

function addColor(data: any, colors: string[]): any {
    return data.map((item: any, index: number) => {
        return {...item, fill: colors[index]}
    })
}

type data = {
    state: string
    value: number
}

function professionalFromBarToRadial(data: any): any {
    const result: Record<string, number> = {}

    data.forEach((item: data) => {
        result[item.state] = item.value
    })

    return [result]
}
