"use client";

import { useState, useRef } from 'react';
import { Button } from "@/components/ui/button";
import { Upload, Image as ImageIcon, X } from 'lucide-react';
import { useToast } from "@/hooks/use-toast";
// Import Next.js Image component
import Image from 'next/image';

const ImageUpload = ({ activityId, onSuccess }) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef(null);
  const { toast } = useToast();
  
  // Add the handleFileChange function implementation
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        toast({
          title: "Filen er for stor",
          description: "Maksimal filstørrelse er 5MB",
          variant: "destructive",
        });
        return;
      }

      if (!['image/jpeg', 'image/png', 'image/gif'].includes(file.type)) {
        toast({
          title: "Ugyldig filtype",
          description: "Kun JPG, PNG og GIF er støttet",
          variant: "destructive",
        });
        return;
      }

      setSelectedFile(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };
  
  // Add the uploadImage function implementation
  const uploadImage = async () => {
    if (!selectedFile) return;

    setUploading(true);
    const reader = new FileReader();
    
    reader.onload = async () => {
      const base64Data = reader.result.split(',')[1]; // Extract base64 data
      
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          throw new Error('Authentication token not found');
        }
        
        const response = await fetch(`http://localhost:8080/api/activity/upload`, {
          method: 'POST',
          headers: {
            'Authorization': token,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            activityId: activityId,
            fileName: selectedFile.name,
            base64ImageData: base64Data
          })
        });

        if (!response.ok) {
          const errorData = await response.text();
          throw new Error(errorData || 'Kunne ikke laste opp bildet');
        }
        
        const result = await response.json();
        
        toast({
          title: "Bilde lastet opp",
          description: "Bildet ble lastet opp til arrangementet",
        });
        setSelectedFile(null);
        setPreview(null);
        if (onSuccess) onSuccess();
      } catch (error) {
        console.error('Error during upload:', error);
        toast({
          title: "Feil under opplasting",
          description: error.message || "Kunne ikke laste opp bildet",
          variant: "destructive",
        });
      } finally {
        setUploading(false);
      }
    };

    reader.onerror = () => {
      toast({
        title: "Feil under lesing av fil",
        description: "Kunne ikke lese bildefilen",
        variant: "destructive",
      });
      setUploading(false);
    };

    reader.readAsDataURL(selectedFile);
  };
  
  // Add the clearSelectedFile function implementation
  const clearSelectedFile = () => {
    setSelectedFile(null);
    setPreview(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  return (
    <div className="space-y-4">
      {!selectedFile ? (
        <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
          <input
            type="file"
            onChange={handleFileChange}
            className="hidden"
            ref={fileInputRef}
            accept="image/jpeg,image/png,image/gif"
          />
          <Button 
            variant="outline" 
            onClick={() => fileInputRef.current?.click()}
            className="w-full py-8 h-auto flex-col space-y-2"
          >
            <Upload className="h-8 w-8 text-muted-foreground" />
            <div className="text-muted-foreground">
              <span className="font-medium">Klikk for å velge bilde</span>
              <p className="text-xs">JPG, PNG eller GIF. Maks 5MB.</p>
            </div>
          </Button>
        </div>
      ) : (
        <div className="border rounded-lg p-4 space-y-4">
          <div className="flex justify-between items-center">
            <div className="flex items-center">
              <ImageIcon className="h-5 w-5 mr-2 text-muted-foreground" />
              <span className="text-sm font-medium">{selectedFile.name}</span>
            </div>
            <Button 
              variant="ghost" 
              size="sm" 
              onClick={clearSelectedFile}
              className="h-8 w-8 p-0 rounded-full"
            >
              <X className="h-4 w-4" />
              <span className="sr-only">Remove file</span>
            </Button>
          </div>
          
          {preview && (
            <div className="relative">
              <div className="relative max-h-48 mx-auto">
                {/* eslint-disable-next-line @next/next/no-img-element */}
                <img 
                  src={preview} 
                  alt={`Preview of ${selectedFile.name}`}
                  className="max-h-48 rounded-md mx-auto"
                />
              </div>
            </div>
          )}
          
          <div className="flex justify-end">
            <Button 
              onClick={uploadImage} 
              disabled={uploading}
              className="px-4"
            >
              {uploading ? "Laster opp..." : "Last opp bilde"}
            </Button>
          </div>
        </div>
      )}
    </div>
  );
};
 
export default ImageUpload;