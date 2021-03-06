package com.polguillen.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.polguillen.app.domain.Socio;
import com.polguillen.app.repository.SocioRepository;
import com.polguillen.app.web.rest.util.HeaderUtil;
import com.polguillen.app.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Socio.
 */
@RestController
@RequestMapping("/api")
public class SocioResource {

    private final Logger log = LoggerFactory.getLogger(SocioResource.class);

    @Inject
    private SocioRepository socioRepository;

    /**
     * POST  /socios -> Create a new socio.
     */
    @RequestMapping(value = "/socios",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Socio> create(@Valid @RequestBody Socio socio) throws URISyntaxException {
        log.debug("REST request to save Socio : {}", socio);
        if (socio.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new socio cannot already have an ID").body(null);
        }
        Socio result = socioRepository.save(socio);
        return ResponseEntity.created(new URI("/api/socios/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("socio", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /socios -> Updates an existing socio.
     */
    @RequestMapping(value = "/socios",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Socio> update(@Valid @RequestBody Socio socio) throws URISyntaxException {
        log.debug("REST request to update Socio : {}", socio);
        if (socio.getId() == null) {
            return create(socio);
        }
        Socio result = socioRepository.save(socio);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("socio", socio.getId().toString()))
                .body(result);
    }

    /**
     * GET  /socios -> get all the socios.
     */
    @RequestMapping(value = "/socios",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Socio>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Socio> page = socioRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/socios", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /socios/:id -> get the "id" socio.
     */
    @RequestMapping(value = "/socios/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Socio> get(@PathVariable Long id) {
        log.debug("REST request to get Socio : {}", id);
        return Optional.ofNullable(socioRepository.findOneWithEagerRelationships(id))
            .map(socio -> new ResponseEntity<>(
                socio,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /socios/:id -> delete the "id" socio.
     */
    @RequestMapping(value = "/socios/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Socio : {}", id);
        socioRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("socio", id.toString())).build();
    }
}
