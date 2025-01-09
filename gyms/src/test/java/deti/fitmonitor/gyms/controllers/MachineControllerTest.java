package deti.fitmonitor.gyms.controllers;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.services.GymService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.services.JwtUtilService;
import deti.fitmonitor.gyms.services.MachineService;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.net.MalformedURLException;
import java.net.URL;

@WebMvcTest(MachineController.class)
@AutoConfigureMockMvc(addFilters = false)
class MachineControllerTest {

    @InjectMocks
    private MachineController machineController;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MachineService machineService;

    @MockBean
    private AmazonS3 amazonS3;

    @MockBean
    private GymService gymService;

    @MockBean
    private JwtUtilService jwtUtilService;

    @MockBean
    private MultipartFile mockFile;

    @MockBean
    private S3Object mockS3Object;

    @MockBean
    private S3ObjectInputStream mockS3ObjectInputStream;

    @BeforeEach
    void setUp() throws MalformedURLException {
        MockitoAnnotations.openMocks(this);
        machineController = new MachineController(machineService, gymService, amazonS3);
        // Configure default behavior for amazonS3 mock if needed
    }



    @Test
    void testCreateMachine() throws IOException {
        String bucketName = "test-bucket";
        ReflectionTestUtils.setField(machineController, "bucketName", bucketName);

        Gym mockGym = new Gym();
        when(gymService.getGymByID(1L)).thenReturn(mockGym);

        MockMultipartFile mockFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});

        URL mockUrl = new URL("https://mock-s3-url.com/test.jpg");
        when(amazonS3.getUrl(eq(bucketName), anyString())).thenReturn(mockUrl);
        when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());

        Machine createdMachine = new Machine("Treadmill", "Cardio machine", true, mockGym);
        createdMachine.setImagePath(mockUrl.toString());
        when(machineService.createMachine(any(Machine.class))).thenReturn(createdMachine);

        ResponseEntity<Machine> response = machineController.createMachine("Treadmill", "Cardio machine", true, mockFile);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockUrl.toString(), response.getBody().getImagePath());
    }

    @Test
    void testUploadImageToS3() throws IOException {
        String bucketName = "test-bucket";
        ReflectionTestUtils.setField(machineController, "bucketName", bucketName);

        MockMultipartFile mockFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});

        URL mockUrl = new URL("https://mock-s3-url.com/test.jpg");
        when(amazonS3.getUrl(eq(bucketName), anyString())).thenReturn(mockUrl);
        when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());

        String imageUrl = machineController.uploadImageToS3(mockFile);

        assertNotNull(imageUrl);
        assertEquals(mockUrl.toString(), imageUrl);
    }




    @Test
    void testGetMachineImage() throws IOException {
        String imagePath = "https://mock-s3-url.com/image.jpg";
        S3Object mockS3Object = mock(S3Object.class);
        S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(new ByteArrayInputStream(new byte[]{1, 2, 3}), null);
        when(mockS3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());
        when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Object);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpeg");
        when(mockS3Object.getObjectMetadata()).thenReturn(metadata);
        when(amazonS3.getObject(any())).thenReturn(mockS3Object);

        ResponseEntity<byte[]> response = machineController.getMachineImage(imagePath);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("image/jpeg", response.getHeaders().getContentType().toString());
        assertArrayEquals(new byte[]{1, 2, 3}, response.getBody());
    }

    @Test
    void testGetMachineImageNotFound() {
        when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());
        when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Object);

        String imagePath = "https://mock-s3-url.com/nonexistent.jpg";
        when(amazonS3.getObject(any())).thenThrow(new com.amazonaws.services.s3.model.AmazonS3Exception("Not Found"));

        ResponseEntity<byte[]> response = machineController.getMachineImage(imagePath);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }


    @Test
    void whenGetMachineByIdReturnMachine() throws Exception {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Machine 1");
        machine.setAvailable(true);
        machine.setDescription("Description 1");

        when(machineService.getMachine(1L)).thenReturn(machine);

        mockMvc.perform(get("/default/api/gyms/machine?id=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Machine 1")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.description", is("Description 1")));
    }

    @Test
    void whenGetAllMachinesReturnAllMachines() throws Exception {
        Machine machine1 = new Machine();
        machine1.setId(1L);
        machine1.setName("Machine 1");
        machine1.setAvailable(true);
        machine1.setDescription("Description 1");

        Machine machine2 = new Machine();
        machine2.setId(2L);
        machine2.setName("Machine 2");
        machine2.setAvailable(false);
        machine2.setDescription("Description 2");

        List<Machine> machines = List.of(machine1, machine2);

        when(machineService.getAllMachines()).thenReturn(machines);

        mockMvc.perform(get("/default/api/gyms/machine/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Machine 1")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[0].description", is("Description 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Machine 2")))
                .andExpect(jsonPath("$[1].available", is(false)))
                .andExpect(jsonPath("$[1].description", is("Description 2")));
    }




    @Test
    void whenGetMachineByUserSubIDReturnMachine() throws Exception {
        Machine machine = new Machine();
        machine.setName("Machine 1");
        machine.setAvailable(true);
        machine.setDescription("Description 1");
        machine.setUserSub("erf42368fek");

        when(machineService.getMachineByUserSub("erf42368fek")).thenReturn(machine);

        mockMvc.perform(get("/default/api/gyms/machine/user?userSub=erf42368fek"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Machine 1"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.userSub").value("erf42368fek"));
    }

}