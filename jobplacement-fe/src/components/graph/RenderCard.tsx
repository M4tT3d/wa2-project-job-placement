import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {PropsWithChildren, ReactNode} from 'react'

export interface CardParameter {
    title: string;
    description?: string;
    className?: string;
    legend?: ReactNode;
}

export function RenderCard(parameter: PropsWithChildren<CardParameter>) {
    return (
        <Card className={parameter.className}>
            <CardHeader>
                <CardTitle>{parameter.title}</CardTitle>
                <CardDescription>{parameter.description}</CardDescription>
                {parameter.legend}
            </CardHeader>
            <CardContent>
                {parameter.children}
            </CardContent>
        </Card>
    )
}