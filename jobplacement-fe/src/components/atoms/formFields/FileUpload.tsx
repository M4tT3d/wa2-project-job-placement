import { Button } from "@/components/atoms/Button"
import { Input } from "@/components/atoms/Input"
import { Label } from "@/components/atoms/Label"
import React, { useState } from "react"

interface FileUploadProps {
  onFileUpload: (file: File) => void
}

const FileUpload: React.FC<FileUploadProps> = ({ onFileUpload }) => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null)

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files ? event.target.files[0] : null
    setSelectedFile(file)
  }

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (selectedFile) {
      onFileUpload(selectedFile)
    } else {
      alert("Nessun file selezionato.")
    }
  }

  return (
    <form id="addDocument" onSubmit={handleSubmit}>
      <div>
        <Label htmlFor="file">File</Label>
        <Input type="file" onChange={handleFileChange} />
      </div>
      <Button type="submit">Upload File</Button>
    </form>
  )
}

export default FileUpload
