package com.project.service;

import com.project.dto.PublisherDto;
import com.project.entity.Publisher;
import com.project.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;

    @Autowired
    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public List<PublisherDto> getAllPublishers() {
        List<Publisher> publishers = publisherRepository.findAll();
        return publishers.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public PublisherDto addPublisher(PublisherDto publisherDto) {
        Publisher publisher = new Publisher(publisherDto.getName());
        publisherRepository.save(publisher);
        publisherDto.setId(publisher.getId());
        return publisherDto;
    }

    public void deletePublisher(Long id) {
        publisherRepository.deleteById(id);
    }

    private PublisherDto mapToDTO(Publisher publisher) {
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(publisher.getId());
        publisherDto.setName(publisher.getName());
        return publisherDto;
    }
}
