package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.mapper.ServiceOrderMapper;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderPhotoRepository;
import github.felipeschwartz.fiber_splice_locator.repository.ServiceOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServiceOrderService {
    private Logger logger = LoggerFactory.getLogger(ServiceOrderService.class.getName());

    private final ServiceOrderRepository serviceOrderRepository;
    private final ServiceOrderPhotoRepository photoRepository;
    private final ServiceOrderMapper serviceOrderMapper;

    public ServiceOrderService(ServiceOrderRepository serviceOrderRepository, ServiceOrderPhotoRepository photoRepository, ServiceOrderMapper serviceOrderMapper, ServiceOrderMapper serviceOrderMapper1) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.photoRepository = photoRepository;
        this.serviceOrderMapper = serviceOrderMapper1;
    }


}
